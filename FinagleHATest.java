
package wavecrest.foundation.service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import com.twitter.finagle.Service;
import com.twitter.finagle.builder.ClientBuilder;
import com.twitter.finagle.builder.ClientConfig.Yes;
import com.twitter.finagle.http.Http;
import com.twitter.finagle.http.Request;
import com.twitter.finagle.http.RequestBuilder;
import com.twitter.finagle.http.Response;
import com.twitter.util.Await;
import com.twitter.util.Duration;
import com.twitter.util.Future;
import com.twitter.util.FutureEventListener;
import com.twitter.util.TimeoutException;

/**
 * @author deva
 *
 */
public class FinagleClientHttpRequest implements ClientHttpRequest {


	public static void main(String[] args) {
		ClientBuilder builder = ClientBuilder.get().codec(Http.get()).hosts("10.10.8.80:80,10.10.8.81:80,10.10.8.82:80").hostConnectionLimit(5);
		Service service = ClientBuilder.safeBuild(builder);
		
		Request httpReq = RequestBuilder.safeBuildGet(RequestBuilder.create().url("http://host/country"));
		final Future response = service.apply(httpReq);
		
		response.addEventListener(new FutureEventListener<Response>() {
			@Override
			public void onFailure(Throwable arg0) {
				System.out.println(arg0.getMessage());
				synchronized(response) {
	                // Notify in the callback thread
	                try {response.notify();} catch (Exception e) {}
	            }
			}
			@Override
			public void onSuccess(Response arg0) {
				System.out.println(arg0.contentString());
				synchronized(response) {
	                // Notify in the callback thread
	                try {response.notify();} catch (Exception e) {}
	            }
			}
		});
		
	  synchronized(response) {
	    	if(!response.isDefined())
	      try {response.wait();} catch (Exception e) {}
	    	
	    }
	  service.close();
	}
	
}
