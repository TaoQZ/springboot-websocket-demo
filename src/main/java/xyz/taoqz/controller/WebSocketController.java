package xyz.taoqz.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.taoqz.endpoint.MyWebSocket;

import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author T
 */
@RestController
@RequestMapping("/web")
public class WebSocketController {

    /**
     * 回顾： httpSession关闭浏览器销毁，再次打开后会生成新的sessionId(会话)
     * @param httpSession
     */
    @GetMapping
    public void demo(HttpSession httpSession) {
        MyWebSocket.broadcast("哈哈哈哈");
    }

    @GetMapping("/getCount")
    public Integer getCount() {
        return MyWebSocket.getCount();
    }

    /**
     * 给指定的用户发送消息
     * @param msg
     * @param session
     * @return
     * @throws IOException
     */
    @GetMapping("/sendMessage/{msg}")
    public String sendMessage(@PathVariable String msg, HttpSession session) throws IOException {
        MyWebSocket myWebSocket = MyWebSocket.httpSessionMyWebSocketMap.get(session.getId());
        String result = "您还没有连接";
        if (null != myWebSocket) {
            myWebSocket.sendMessage(msg);
            return msg;
        }
        return result;
    }

}
