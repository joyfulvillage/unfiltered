package unfiltered.netty.cycle

import org.jboss.netty.handler.codec.http.{
  DefaultHttpRequest, DefaultHttpResponse, HttpResponse=>NHttpResponse}
import org.jboss.netty.channel._
import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import org.jboss.netty.handler.codec.http.HttpVersion._
import unfiltered.netty._
import unfiltered.response.{ResponseFunction,NotFound}
import unfiltered.request.HttpRequest

object Plan {
  type Intent = unfiltered.Cycle.Intent[DefaultHttpRequest,NHttpResponse]
}
/** A Netty Plan for request cycle handling. */
abstract class Plan extends SimpleChannelUpstreamHandler {
  def intent: Plan.Intent
  override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) {
    val request = e.getMessage().asInstanceOf[DefaultHttpRequest]
    val requestBinding = new RecievedMessageBinding(request, ctx, e)

    if (intent.isDefinedAt(requestBinding)) {
      requestBinding.respond(intent(requestBinding))
    } else {
      requestBinding.respond(NotFound)
    }
  }
}

class Planify(val intent: Plan.Intent) extends Plan

object Planify {
  def apply(intent: Plan.Intent) = new Planify(intent)
}