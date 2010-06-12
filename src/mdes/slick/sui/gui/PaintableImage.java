package mdes.slick.sui.gui;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Hashtable;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Image;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.ImageData;
import org.newdawn.slick.opengl.InternalTextureLoader;

/**
 * An extension of Slick's Image class which can 
 * be drawn on using the java.awt.Graphics2D. 
 * <p>
 * After the AWT drawing has been finished the
 * refresh() method must be called to update any
 * changes. Currently refreshing the image is slow.
 * <p>
 * <b>Example:</b>
 * <pre><code>
 * //init Slick
 * public void init(GameContainer c) throws SlickException {
 *   image = new PaintableImage(200, 200);
 *   Graphics2D g2d = image.getGraphics();
 *   g2d.setColor(java.awt.Color.red);
 *   g2d.drawOval(0, 0, 50, 50);
 *   image.refresh();
 * }
 * 
 * //render using Slick graphics.
 * public void render(GameContainer c, Graphics g) {
 *   //rendering
 *   g.drawImage(10, 10, image);
 * </code></pre>
 * <p>
 * Currently OPAQUE images and mipmaps are not supported.
 *
 * @author davedes
 */
public class PaintableImage extends Image {
    
    /** The BufferedImage backed by this Slick Image. */
    protected BufferedImage awtImage;
    
    /** A reusable ByteBuffer. */
    private ByteBuffer tmpbuffer;
    
    /** The alpha ColorModel used to create the BufferedImage. */
    protected final ColorModel glAlphaColorModel = new ComponentColorModel(
                        ColorSpace.getInstance(ColorSpace.CS_sRGB),
                        new int[] {8,8,8,8},
                        true,
                        false,
                        ComponentColorModel.TRANSLUCENT,
                        DataBuffer.TYPE_BYTE);
    
    /** Creates a new PaintableImage with the specified width and height. */
    public PaintableImage(int width, int height) throws SlickException {
        super(new ImageBuffer(width++, height++));
                
        //TODO: remove increment for width/height bug
        
        //gets 2 fold texture size
        int texWidth = getTexture().getTextureWidth();
        int texHeight = getTexture().getTextureHeight();
        
        WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,texWidth,texHeight,4,null);
        awtImage = new BufferedImage(glAlphaColorModel,raster,false,new Hashtable());
        
        //initially empty
        Graphics2D g = (Graphics2D)awtImage.getGraphics();
        g.setColor(new java.awt.Color(0f,0f,0f,0f));
        g.fillRect(0,0,texWidth,texHeight);
        
        // build a byte buffer from the temporary image 
        // that can be used by OpenGL to produce a texture.
        byte[] raw = ((DataBufferByte)awtImage.getRaster().getDataBuffer()).getData(); 
        
        tmpbuffer = ByteBuffer.allocateDirect(raw.length);
        tmpbuffer.order(ByteOrder.nativeOrder());
        ByteBuffer scratch = ByteBuffer.allocateDirect(raw.length); 
        scratch.order(ByteOrder.nativeOrder()); 
        scratch.put(raw, 0, raw.length); 
        scratch.flip();
        
        //create ImageData and pass it to TextureLoader to change the texture
        EmptyData data = new EmptyData(width, height, texWidth, texHeight, 32, scratch);
        try { 
            //texture from Image is protected
            
            this.setTexture(InternalTextureLoader.get().getTexture(data, GL11.GL_LINEAR));
           // texture = TextureLoader.get().getTexture(data, GL11.GL_LINEAR);
            
            //change width to zero so the sizes are reinitialized
            width = 0;
        }
        catch (IOException e) {
            throw new SlickException("Error loading PaintableImage", e);
        }
    }
    
    @Override
    public int getHeight() {
        return super.getHeight()-1;
    }
    
    @Override
    public int getWidth() {
        return super.getWidth()-1;
    }
    
    public void clear() {
        getGraphics2D().clearRect(0, 0, super.getWidth(), super.getHeight());
    }
    
    /** 
     * Gets the Graphics2D context for this Image. After using the Graphics2D, 
     * refresh() must be called to register the changes.
     *
     * @returns java.awt.Graphics2D for rendering in Slick
     */
    public Graphics2D getGraphics2D() {
        return (Graphics2D)awtImage.getGraphics();
    }
    
    /** 
     * Refreshes the whole texture. Support for dirty pixels and other
     * optimizations will come soon.
     */
    public void refresh() {        
        //binds new texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, getTexture().getTextureID());
        
        // build a byte buffer from the temporary image 
        // that can be used by OpenGL to produce a texture.
        byte[] raw = ((DataBufferByte)awtImage.getRaster().getDataBuffer()).getData(); 
        ByteBuffer scratch = tmpbuffer;
        scratch.clear();
        scratch.put(raw, 0, raw.length); 
        scratch.flip();
        
        //TODO: dirty regions only
        //for now we produce the whole texture
        GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0,
                0, 0, getTexture().getTextureWidth(), getTexture().getTextureHeight(), 
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, scratch);
    }
    
    /** A utility class for loading textures. */
    private class EmptyData implements ImageData {
        
        private final int depth,width,height,texWidth,texHeight;
        private final ByteBuffer data;
      
        public EmptyData(int width, int height, int texWidth, int texHeight, int depth, ByteBuffer data) {
            this.width=width;
            this.height=height;
            this.depth=depth;
            this.data=data;
            this.texWidth=texWidth;
            this.texHeight=texHeight;
        }
            
        @Override
        public int getWidth() { return width; }
        @Override
        public int getHeight() { return height; }
        @Override
        public int getTexWidth() { return texWidth; }
        @Override
        public int getTexHeight() { return texHeight; }
        @Override
        public int getDepth() { return depth; }
        @Override
        public ByteBuffer getImageBufferData() { return data; }
    }
}