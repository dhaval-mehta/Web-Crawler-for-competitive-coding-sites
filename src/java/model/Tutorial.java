/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Dhaval
 */
@Entity
@Table(name = "tutorial")
public class Tutorial implements Serializable
{

    @Id
    String name;

    String content;
}
