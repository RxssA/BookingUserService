package ie.atu.usersevice;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.WebFilter;


@WebFilter("/api/*")
public class TokenFilter extends OncePerRequestFilter {
}
