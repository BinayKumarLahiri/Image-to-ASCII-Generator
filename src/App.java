public class App {
    public static void main(String[] args) {
        /**
         * image specifies the path of the image
         * There are two example images in the Asset folder
         * Asset
         * -> 1. butterfly.jpg
         * -> 2. tanjiro.jpg
         * you can try any of the image or use your own image
         */
        String image = "../asset/butterfly.jpg";
        Renderer renderer = new Renderer(image);
        renderer.draw(false); // Draw in Black & White
        // renderer.draw(true); // Draw in Colour
    }
}