package model;

/**
 * This class stores all information about a Professor who 
 * are the answers in this game.

 * @version Nov 7, 2018
 *
 * This work complies with the JMU Honor Code
 */
public class Professor
{
  
  String name;
  String image;
  
  public Professor(String name) {
    this.name = name;
  }
  
  public void setImage(String image) {
	this.image = image;
  }
  
  public String getImage() {
	  return image;
  }
  
  public String toString() {
    return "Dr. " + name;
  }

}
