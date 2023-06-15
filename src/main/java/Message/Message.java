package Message;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

import static DataBase.DbCtrl.isLoginKeyValid;
@WebServlet("/message")
public class Message extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (Utils.CheckLogin.CheckLoginKey(request)){
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/message.html");
            dispatcher.forward(request, response);
        }else{
            response.sendRedirect("/login");
        }

    }
}
