package com.krylov.tasktracker.tasktracker_rest_web_service.security.jwt.service;

import com.krylov.tasktracker.tasktracker_rest_web_service.entity.RoleEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.security.jwt.JwtAuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret.signing.key}")
    private String secretSigningKey;

    @Value("${jwt.token.life.time}")
    private long tokenLifeTime;


    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtServiceImpl(@Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Optional<String> createToken(String userName, List<RoleEntity> roles) {
        Claims claims = Jwts.claims();
        claims.setSubject(userName);
//        claims.put("roles", getRoleNames(roles));

        Date dateNow = new Date();
        Date expiredDate = new Date(dateNow.getTime() + tokenLifeTime);
        String result = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(dateNow)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, secretSigningKey)
                .compact();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<String> resolveToken(HttpServletRequest httpServletRequest) {
        String bearerToken = httpServletRequest.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return Optional.ofNullable(bearerToken.substring(7, bearerToken.length()));
        }
        return Optional.empty();
    }

    @Override
    public boolean isValidToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretSigningKey).parseClaimsJws(token);
            if (claims.getBody().getExpiration().before(new Date())) {
                return false;
            }
            return true;
        } catch (Exception e){
            return false;
        }
    }

    @Override
    public Optional<Authentication> getAuthentication(String token) {
        Optional<String> optionalUserName = getUserName(token);
        if (optionalUserName.isEmpty()) return Optional.empty();

        UserDetails userDetails = userDetailsService.loadUserByUsername(optionalUserName.get());
        return Optional.of(new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities()));
    }

    private Optional<String> getUserName(String token) {
        try {
            return Optional.ofNullable(Jwts.parser().setSigningKey(secretSigningKey).parseClaimsJws(token).getBody().getSubject());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private List<String> getRoleNames(List<RoleEntity> roles) {
        List<String> result =
                roles.stream().map(roleEntity -> roleEntity.getName().toString()).collect(Collectors.toList());
        return result;
    }
}
