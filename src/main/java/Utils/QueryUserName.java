package Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

import static DataBase.DbCtrl.getUserNameById;

@WebServlet("/queryUserName")
public class QueryUserName extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (Utils.CheckLogin.CheckLoginKey(req)){
            String UserId = req.getParameter("userid");
            String UserName =null;
            try {
                UserName= getUserNameById(UserId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (UserName!=null){
                String RespJson = "{\n" +
                        "  \"success\": true,\n" +
                        "  \"message\": " +"\""+UserName+
                        "\"\n}";
                resp.setStatus(200);
                resp.setCharacterEncoding("UTF-8");
                resp.getWriter().println(RespJson);
            }else{
                String RespJson = "{\n" +
                        "  \"success\": false,\n" +
                        "  \"message\": \"用户不存在\"\n" +
                        "}";
                resp.setStatus(200);
                resp.setCharacterEncoding("UTF-8");
                resp.getWriter().println(RespJson);
            }
        }
    }
}
