package Utils;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import static DataBase.DbCtrl.getUserMessages;
import static DataBase.DbCtrl.isLoginKeyValid;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entity.Message;


@WebServlet("/getMessage")
public class GetMessage extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (Utils.CheckLogin.CheckLoginKey(request)) {
            String userIdParam = request.getParameter("userid");
            String userId = null;
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("userId")) {
                    userId = cookie.getValue();
                }
            }
            List<Message> messages;
            try {
                messages = getUserMessages(Integer.parseInt(userId), Integer.parseInt(userIdParam));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            JsonArray jsonArray = new JsonArray();
            for (Message message : messages) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("messageId", message.getMessageId());
                jsonObject.addProperty("sendName", message.getSendName());
                jsonObject.addProperty("sendUserId", message.getSendUserId());
                jsonObject.addProperty("receiveUserName", message.getReceiveUserName());
                jsonObject.addProperty("receiveUserId", message.getReceiveUserId());
                jsonObject.addProperty("messageRow", message.getMessageRow());
                jsonObject.addProperty("sendTime", message.getSendTime().toString());
                jsonObject.addProperty("isDelete", message.getIsDelete());
                jsonArray.add(jsonObject);
            }
            out.print(jsonArray.toString());
        }
    }
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        if (Utils.CheckLogin.CheckLoginKey(request)){
//            String userIdParam = request.getParameter("userid");
//            String userId = null;
//            for (Cookie cookie : request.getCookies()) {
//                if (cookie.getName().equals("userId")) {
//                    userId = cookie.getValue();
//                }
//            }
//            List<Message> messages;
//            try {
//                messages = getUserMessages(Integer.parseInt(userId), Integer.parseInt(userIdParam));
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//            response.setContentType("application/json");
//            response.setCharacterEncoding("UTF-8");
//            PrintWriter out = response.getWriter();
//            out.println("[");
//            for (Message message : messages) {
//                out.println("{");
//                out.println("\"messageId\": " + message.getMessageId() + ",");
//                out.println("\"sendName\": \"" + message.getSendName() + "\",");
//                out.println("\"sendUserId\": " + message.getSendUserId() + ",");
//                out.println("\"receiveUserName\": \"" + message.getReceiveUserName() + "\",");
//                out.println("\"receiveUserId\": " + message.getReceiveUserId() + ",");
//                out.println("\"messageRow\": \"" + message.getMessageRow() + "\",");
//                out.println("\"sendTime\": \"" + message.getSendTime() + "\",");
//                out.println("\"isDelete\": " + message.getIsDelete());
//                out.println("},");
//            }
//            out.println("]");
//        }
//    }
}
