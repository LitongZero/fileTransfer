package com.ltz.controller;

import com.ltz.entity.Myfile;
import com.ltz.util.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FileController {

    @Value("${upLoadPath:./}")
    private String upLoadPath;


    @RequestMapping("/")
    public String file(Model model) {
        File file = new File(upLoadPath);
        File[] files = file.listFiles();
        List<Myfile> result = new ArrayList<>(files.length);
        for (File f : files) {
            if (f.isDirectory()) {
                result.add(new Myfile(f.getName(), "文件夹"));
            } else {
                result.add(new Myfile(f.getName(), FileUtil.formatFileSize(f.length())));
            }
        }
        model.addAttribute("files", result);
        return "index";
    }

    @RequestMapping("/download/{fileName}")
    public String downLoad(Model model, HttpServletResponse response, @PathVariable("fileName") String fileName) {
        File file = new File(new File(upLoadPath).getAbsolutePath(), fileName);
        if (!file.exists()) {
            model.addAttribute("msg", "下载失败");
            return "index";
        } else if (file.isDirectory()) {
            model.addAttribute("msg", "文件夹不能直接下载");
            return "index";
        } else {
            response.setContentType("application/force-download");
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                byte[] buff = new byte[1024];
                int len;
                ServletOutputStream os = response.getOutputStream();
                while ((len = bis.read(buff)) != -1) {
                    os.write(buff, 0, len);
                    os.flush();
                }
            } catch (IOException e) {
                model.addAttribute("msg", "下载失败");
                return "index";
            }

            model.addAttribute("msg", "下载成功");
            return "index";
        }
    }

    @RequestMapping({"/upload"})
    public String upLoad(Model model, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            model.addAttribute("msg", "还未选择任何文件");
            return "/index";
        } else {
            try {
                String name = file.getOriginalFilename();
                File saveFile = new File(new File(upLoadPath).getAbsolutePath(), name);
                file.transferTo(saveFile);
                System.out.println(file.getOriginalFilename() + "上传成功" + saveFile.getPath());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(file.getOriginalFilename() + "保存失败");
            }

            model.addAttribute("msg", "上传成功");
            return "redirect:/";
        }
    }
}
