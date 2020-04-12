package geektime.im.lecture.ws.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 若干个心跳周期内没有消息的收发,关闭连接。
 */
@ChannelHandler.Sharable
@Component
public class CloseIdleChannelHandler extends ChannelDuplexHandler {

    private static final Logger logger = LoggerFactory.getLogger(CloseIdleChannelHandler.class);

    @Autowired
    private WebsocketRouterHandler websocketRouterHandler;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            //all_idle指的是读idle和写idle同时满足，另外还可以单独再处理读idle和写idle
            if (event.state() == IdleState.ALL_IDLE) {
                logger.info("connector no receive ping packet from client,will close.,channel:{}", ctx.channel());
                websocketRouterHandler.cleanUserChannel(ctx.channel());
                ctx.close();
            }else if(event.state() == IdleState.READER_IDLE){
                System.out.println("触发读idle");
            }else if(event.state() == IdleState.WRITER_IDLE){
                System.out.println("触发写Idle");
            }
        }
    }
}
