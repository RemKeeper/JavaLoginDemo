package loginweb;



import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.SQLException;
import static DataBase.DbCtrl.loginUser;


@WebServlet("/login")
public class login extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (Utils.CheckLogin.CheckLoginKey(request)){
           response.sendRedirect("/message");
        }else{
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/login.html");
            dispatcher.forward(request, response);
        }
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws IOException {
        String userName = request.getParameter("userName");
        String userKey = request.getParameter("userKey");
        LoginMessage loginMessage = null;
        try {
            loginMessage = loginUser(userName, userKey);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (loginMessage!=null) {
            HttpSession session = request.getSession();
            session.setAttribute("userId", loginMessage.UserId);
            session.setAttribute("loginKey",loginMessage.LoginKey);
            Cookie userIdCookie = new Cookie("userId", String.valueOf(loginMessage.UserId));
            Cookie loginKeyCookie = new Cookie("loginKey",loginMessage.LoginKey);
            userIdCookie.setMaxAge(60 * 60 * 24 * 30); // 设置有效期为30天
            loginKeyCookie.setMaxAge(60*60*24*30);
            resp.addCookie(userIdCookie);
            resp.addCookie(loginKeyCookie);
            String RespJson = "{\n" +
                    "  \"success\": true,\n" +
                    "  \"message\": \"login success\"\n" +
                    "}";
            resp.setStatus(200);
            resp.getWriter().println(RespJson);
        } else {
            String RespJson = "{\n" +
                    "  \"success\": false,\n" +
                    "  \"message\": \"登陆失败，账号或密码错误\"\n" +
                    "}";
            resp.setStatus(200);
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().println(RespJson);
        }
    }
}

