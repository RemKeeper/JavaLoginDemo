package loginweb;

import DataBase.DbCtrl;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/register")
public class register extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/register.html");
        dispatcher.forward(request, response);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        System.out.println(username);
        System.out.println(password);
        System.out.println(email);
        // 对用户进行注册逻辑处理
        boolean success = true;
        String message = "";
        if (username == null || username.isEmpty() || password == null || password.isEmpty() || email == null || email.isEmpty()) {
            success = false;
            message = "请填写完整信息";
        }else {
            // 在这里进行用户注册操作，将结果保存到数据库中
            try {
                int UserId=DbCtrl.Register(username,password,email);
                success=true;
                message="注册成功,您的用户Id为  "+UserId+"  请牢记";
            } catch (Exception e) {
                success = false;
                message = "注册失败，请稍后重试";
                e.printStackTrace();
            }
        }

        // 返回处理结果给客户端
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out =response.getWriter();
        out.print("{\"success\": " + success + ", \"message\": \"" + message + "\"}");
        out.flush();
        out.close();
    }
}
