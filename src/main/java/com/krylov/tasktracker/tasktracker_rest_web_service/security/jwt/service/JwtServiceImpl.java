package com.krylov.tasktracker.tasktracker_rest_web_service.security.jwt.service;

import com.krylov.tasktracker.tasktracker_rest_web_service.dto.jwt.JwtDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.exception.JwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

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
    public JwtDto authenticateAndCreateToken(AuthenticationManager authenticationManager, String userName, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password));

            Claims claims = Jwts.claims();
            claims.setSubject(userName);

            Date dateNow = new Date();
            Date expiredDate = new Date(dateNow.getTime() + tokenLifeTime);
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(dateNow)
                    .setExpiration(expiredDate)
                    .signWith(SignatureAlgorithm.HS256, secretSigningKey)
                    .compact();
            return new JwtDto(userName, token);

        } catch (Exception e) {
            throw new JwtException("Unable to authenticate and create JWT");
        }
    }

    @Override
    public String resolveToken(HttpServletRequest httpServletRequest) {
        try {
            String bearerToken = httpServletRequest.getHeader("Authorization");
            if (bearerToken.startsWith("Bearer ")) {
                return bearerToken.substring(7, bearerToken.length());
            }
            throw new JwtException("Unable to resolve JWT");
        } catch (Exception e) {
            throw new JwtException("Unable to resolve JWT");
        }
    }

    @Override
    public boolean isValidToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretSigningKey).parseClaimsJws(token);
            if (claims.getBody().getExpiration().before(new Date())) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Authentication getAuthentication(String token) {
        try {
            String userName = Jwts.parser().setSigningKey(secretSigningKey).parseClaimsJws(token).getBody().getSubject();
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
            return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        } catch (Exception e){
            throw new JwtException("Unable to get authentication based on JWT");
        }
    }
}
