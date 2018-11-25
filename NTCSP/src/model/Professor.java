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
  String mouthImageName;
  String headImageName;
  String audioName;

  public Professor(String name)
  {
    this.name = name;
    String imageTemp = name.toLowerCase();
    this.imageName = imageTemp + ".jpg";
    this.headImageName = imageTemp.substring(0, 1).toUpperCase()
            + imageTemp.substring(1,imageTemp.length()) + ".png";
    this.mouthImageName = imageTemp.substring(0, 1).toUpperCase()
            + imageTemp.substring(1,imageTemp.length()) + "Mouth.png";
    this.audioName = name + ".wav";
  }
  
  public String toString() { return "Dr. " + name; }

  public String getImage() {
    return imageName;
  }

  public String getHeadImageName() {
    return headImageName;
  }

  public String getMouthImageName() {
    return mouthImageName;
  }

  public String getAudioName() { return audioName; }
}
