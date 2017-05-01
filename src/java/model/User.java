/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import security.Role;

/**
 *
 * @author Dhaval
 */
@Entity
@Table(name = "user_master")
public class User implements Serializable
{

    @Id
    private String username;
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "role_table", joinColumns = @JoinColumn(name = "username"))
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "role")
    private Set<Role> roles;

    public User()
    {
	roles = new HashSet<>();
    }

    public String getUsername()
    {
	return username;
    }

    public void setUsername(String username)
    {
	this.username = username;
    }

    public String getPassword()
    {
	return password;
    }

    public void setPassword(String password)
    {
	this.password = password;
    }

    public Set<Role> getRoles()
    {
	return roles;
    }

    public void setRoles(Set<Role> roles)
    {
	this.roles = roles;
    }
}
