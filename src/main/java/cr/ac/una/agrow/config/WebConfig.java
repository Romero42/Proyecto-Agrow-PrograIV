package cr.ac.una.agrow.config;

import cr.ac.una.agrow.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionInterceptor())
                .addPathPatterns("/**") // Aplicar a todas las rutas
                .excludePathPatterns("/", "/login", "/index/validate", "/css/**", "/js/**", "/img/**", "/error"); // Excluir login y recursos estáticos
    }

    private static class SessionInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            HttpSession session = request.getSession(false); // No crear nueva sesión si no existe
            User loggedInUser = (session != null) ? (User) session.getAttribute("loggedInUser") : null;

            if (loggedInUser == null) {
                // Si no hay usuario y no es una de las rutas excluidas, redirigir al login
                String requestURI = request.getRequestURI();
                if (!requestURI.equals("/") && !requestURI.equals("/login") && !requestURI.equals("/index/validate") &&
                        !requestURI.startsWith("/css/") && !requestURI.startsWith("/js/") && !requestURI.startsWith("/img/")) {

                    System.out.println("SessionInterceptor: No user in session for " + requestURI + ". Redirecting to /login.");
                    response.sendRedirect(request.getContextPath() + "/login");
                    return false; // Detener la ejecución
                }
            }
            return true; // Continuar con la ejecución
        }

        @Override
        public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        }
    }
}