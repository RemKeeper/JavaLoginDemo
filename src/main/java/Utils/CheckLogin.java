package Utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

import static DataBase.DbCtrl.isLoginKeyValid;

public class CheckLogin {
    public static boolean CheckLoginKey(HttpServletRequest request){
        String userId = null;
        String loginKey = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("userId")) {
                    userId = cookie.getValue();
                } else if (cookie.getName().equals("loginKey")) {
                    loginKey = cookie.getValue();
                }
            }
            if (userId != null && loginKey != null) {
                boolean isLoginValid = false;
                try {
                    isLoginValid = isLoginKeyValid(userId, loginKey);
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
                return isLoginValid;
            }else{
                return false;
            }
        }else {
            return false;
        }
    }
}
