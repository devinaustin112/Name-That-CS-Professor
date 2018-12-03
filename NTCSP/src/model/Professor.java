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
  String audioNameCorrect;
  String audioNameIncorrect;

  /**
   * Explicit Value Constructor
   *
   * @param name The name of the professor
   */
  public Professor(String name)
  {
    this.name = name;
    String imageTemp = name.toLowerCase();
    this.imageName = imageTemp + ".jpg";
    this.headImageName = imageTemp.substring(0, 1).toUpperCase()
            + imageTemp.substring(1,imageTemp.length()) + ".png";
    this.mouthImageName = imageTemp.substring(0, 1).toUpperCase()
            + imageTemp.substring(1,imageTemp.length()) + "Mouth.png";
    this.audioNameCorrect = name + "Correct.wav";
    this.audioNameIncorrect = name + "Incorrect.wav";
  }

  /**
   * Formats the professor's name.
   *
   * @return A String containing the professor's name.
   */
  public String toString() { return "Dr. " + name; }

  /**
   * Gets the professor's image.
   *
   * @return The professor's image.
   */
  public String getImage() {
    return imageName;
  }

  /**
   * Gets the name of the image containing the professor's head.
   *
   * @return The name of the image containing the professor's head.
   */
  public String getHeadImageName() {
    return headImageName;
  }

  /**
   * Gets the name of the image containing the professor's mouth.
   *
   * @return The name of the image containing the professor's mouth.
   */
  public String getMouthImageName() {
    return mouthImageName;
  }

  /**
   * Gets the professor's correct audio file.
   *
   * @return The professor's correct audio file.
   */
  public String getAudioNameCorrect() { return audioNameCorrect; }

  /**
   * Gets the professor's incorrect audio file.
   *
   * @return The professor's incorrect audio file.
   */
  public String getAudioNameIncorrect() { return audioNameIncorrect; }

}
