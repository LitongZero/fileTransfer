package com.ltz.controller;

import com.ltz.entity.Myfile;
import com.ltz.util.FileUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FileController {

    private static String UPLOAD_PATH = "./";


    @RequestMapping("/")
    public String file(Model model) {
        File file = new File("./");
        File[] files = file.listFiles();
        List<Myfile> result = new ArrayList<>(files.length);
        for (File f :
                files) {
            result.add(new Myfile(f.getName(), FileUtil.formatFileSize(f.length())));
        }
        model.addAttribute("files", result);
        return "index";
    }

    @RequestMapping("/download/{fileName}")
    public String downLoad(Model model, HttpServletResponse response, @PathVariable("fileName") String fileName) {
        File file = new File("./" + fileName);
        if (!file.exists()) {
            model.addAttribute("msg", "下载失败");
            return "/index";
        } else {
            response.setContentType("application/force-download");

            try {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));

                try {
                    byte[] buff = new byte[1024];
                    ServletOutputStream os = response.getOutputStream();

                    int i;
                    while ((i = bis.read(buff)) != -1) {
                        os.write(buff, 0, i);
                        os.flush();
                    }
                    os.close();
                } catch (Throwable throwable) {
                    try {
                        bis.close();
                    } catch (Throwable var9) {
                        throwable.addSuppressed(var9);
                    }

                    throw throwable;
                }

                bis.close();
            } catch (IOException var11) {
                model.addAttribute("msg", "下载失败");
                return "index";
            }

            model.addAttribute("msg", "下载成功");
            return "index";
        }
    }

    @RequestMapping("/upload")
    public String upLoad(Model model, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            model.addAttribute("msg", "还未选择任何文件");
            return "/index";
        } else {
            String name = file.getOriginalFilename();

            try {
                FileOutputStream fos = new FileOutputStream(UPLOAD_PATH + name);
                fos.write(file.getBytes());
                fos.close();
                System.out.println(file.getOriginalFilename() + "上传成功");
            } catch (IOException e) {
                System.out.println(file.getOriginalFilename() + "保存失败");
                e.printStackTrace();
            }

            model.addAttribute("msg", "上传成功");
            return "redirect:/";
        }
    }

}
