/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package othi.thg.client.entities;

/**
 *
 * @author Steve
 */
public abstract class THGEntity {

    int id;
    String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
