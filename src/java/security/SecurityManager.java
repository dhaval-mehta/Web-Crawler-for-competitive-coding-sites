/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import model.User;

/**
 *
 * @author Dhaval
 */
public class SecurityManager
{

    private static final int VALIDITY = 1;
    private static final Key KEY = MacProvider.generateKey();
    private static final String ISSUER = "webservices-dhavalmehta.rhcloud.com";
    private static final Map<String, User> TOKEN_TO_USER_MAP = new HashMap<>();

    public static String issueToken(User user)
    {
	Calendar calendar = new GregorianCalendar();
	calendar.setTime(new Date());
	calendar.add(Calendar.DATE, VALIDITY);

	String jwtToken = Jwts.builder()
		.setSubject(user.getUsername())
		.setIssuer(ISSUER)
		.setIssuedAt(new Date())
		.setExpiration(calendar.getTime())
		.signWith(SignatureAlgorithm.HS512, KEY)
		.compact();

	TOKEN_TO_USER_MAP.put(jwtToken, user);

	return jwtToken;
    }

    public static void validateToken(String token)
    {
	Jwts.parser().setSigningKey(KEY).parseClaimsJws(token);
    }

    public static User getUser(String token)
    {
	return TOKEN_TO_USER_MAP.get(token);
    }
}
