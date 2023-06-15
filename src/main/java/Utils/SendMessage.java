package Utils;

import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static DataBase.DbCtrl.sendMessage;
@WebServlet("/SendMessage")
public class SendMessage extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (Utils.CheckLogin.CheckLoginKey(request)){
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String json = reader.readLine();
            System.out.println("Received JSON data: " + json);
            // 将JSON字符串解析为JSONObject对象
            JSONObject message = null;
            try {
                message = new JSONObject(json);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            // 从JSONObject中获取需要写入数据库的数据
            String sendName = message.optString("SendName");
            int sendUserId = 0;
            try {
                sendUserId = message.getInt("SendUserId");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            String receiveUserName = message.optString("ReceiveUserName");
            int receiveUserId = 0;
            try {
                receiveUserId = message.getInt("ReceiveUserId");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            String messageRow = message.optString("MessageRow");

            // 调用sendMessage方法将数据写入数据库，并根据结果返回响应
            if (sendMessage(sendUserId,  receiveUserId, messageRow)) {
                String responseJson = String.format("{\"success\": true, \"message\": \"%s\"}", "发送成功");
                response.setStatus(200);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().println(responseJson);
            } else {
                String responseJson = String.format("{\"success\": false, \"message\": \"%s\"}", "发送失败");
                response.setStatus(500);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().println(responseJson);
            }
        }
    }
}
