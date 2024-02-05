package ru.netology.cloudservice.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import ru.netology.cloudservice.entities.User;
import ru.netology.cloudservice.exceptions.NoSuchUserException;
import ru.netology.cloudservice.repositories.UsersRepository;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private final static String SECRET_KEY = generateSecretKey();
    private UsersRepository usersRepository;

    public JwtTokenProvider(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 3600000);

        String token = Jwts.builder()
                           .setSubject(user.getUsername())
                           .setIssuedAt(now)
                           .setExpiration(expiryDate)
                           .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                           .compact();

        user.setAuthToken(token);
        user.setRoles(new HashSet<>(Arrays.asList("ROLE_USER")));
        usersRepository.save(user);

        return token;
    }

    public String getUserLoginFromToken(String token) {
        return Jwts.parser()
                   .setSigningKey(SECRET_KEY)
                   .parseClaimsJws(token)
                   .getBody()
                   .getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Auth-Token");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        String login = getUserLoginFromToken(token);
        List<String> roles = getRoles(login);
        Collection<? extends GrantedAuthority> authorities =
                roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(login, null, authorities);
    }

    private List<String> getRoles(String login) {
        Optional<User> user = usersRepository.findByLogin(login);
        if (!user.isPresent()) {
            throw new NoSuchUserException("Check request data and try again.");
        }
        List<String> roles = new ArrayList<>(user.get().getRoles());
        return roles;
    }

    private static String generateSecretKey() {
        byte[] keyBytes = new byte[64];
        new SecureRandom().nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes);
    }
}
