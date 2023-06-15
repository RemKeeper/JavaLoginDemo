package loginweb;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@WebServlet("/image/*")
public class Resource extends HttpServlet {
    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String RequestUrl=req.getRequestURI();
        String filePath = RequestUrl.substring(RequestUrl.indexOf("/image/") + 7);
        String realPath = getServletContext().getRealPath(filePath);
        File file = new File(realPath);
        if (file.exists()) { //如果文件存在
            FileInputStream inputStream = new FileInputStream(file); //创建一个文件输入流
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                resp.getOutputStream().write(buffer, 0, len); //将文件写入响应输出流
            }
            inputStream.close();
        } else { //如果文件不存在，返回404错误
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}

