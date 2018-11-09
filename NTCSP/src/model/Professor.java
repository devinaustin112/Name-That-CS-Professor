package model;

/**
 * This class stores all information about a Professor, who
 * are the answers in this game.

 * @version Nov 7, 2018
 *
 * This work complies with the JMU Honor Code
 */
public class Professor
{
  
  String name;
  String imageName;

  public Professor(String name) {
    this.name = name;
    String imageTemp = name.toLowerCase();
    this.imageName = imageTemp + ".jpg";
  }
  
  public String toString() {
    return "Dr. " + name;
  }

  public String getImage() {
    return imageName;
  }

}
