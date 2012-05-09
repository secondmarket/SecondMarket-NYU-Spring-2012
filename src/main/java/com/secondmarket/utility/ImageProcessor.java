package com.secondmarket.utility;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * 
 * copyright SecondMarket, Inc. 2011
 * 
 * @author mcitowicki
 * @created Jul 18, 2011
 * 
 * @Modified by Ming Li
 * 
 */

public class ImageProcessor {
	private static final int[] THUMBNAIL_SIZES = { 50, 150 };

	public List<byte[]> processImage(String imageUrl) {
		int xPos = 0;
		int yPos = 0;
		InputStream inputStream = null;
		List<byte[]> images = new ArrayList<byte[]>();
		try {
			BufferedImage origImg = ImageIO.read(new URL(imageUrl));
			int destType = origImg.getType() == BufferedImage.TYPE_INT_ARGB
					|| origImg.getType() == BufferedImage.TYPE_INT_ARGB
					|| origImg.getType() == BufferedImage.TYPE_4BYTE_ABGR
					|| origImg.getType() == BufferedImage.TYPE_4BYTE_ABGR_PRE ? BufferedImage.TYPE_INT_ARGB
					: BufferedImage.TYPE_INT_RGB;
			BufferedImage tempImg = new BufferedImage(origImg.getWidth(),
					origImg.getHeight(), destType);
			Graphics2D tempG = tempImg.createGraphics();
			tempG.drawImage(origImg, 0, 0, null);
			tempG.dispose();
			origImg = tempImg;
			int origW = origImg.getWidth();
			int origH = origImg.getHeight();
			int dimension = Math.max(origImg.getWidth(), origImg.getHeight());
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			if (origW > origH) {
				yPos = (int) Math.floor((origW - origH) / 2);
			} else if (origH > origW) {
				xPos = (int) Math.floor((origH - origW) / 2);
			}
			BufferedImage thumbImg = new BufferedImage(dimension, dimension,
					origImg.getType());
			Graphics2D g = thumbImg.createGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, dimension, dimension);
			if (yPos != 0) {
				Color[] colors = getBorderColors(origImg, false);
				g.setColor(colors[0]);
				g.fillRect(0, 0, dimension, yPos);
				g.setColor(colors[1]);
				g.fillRect(0, dimension - yPos - 1, dimension, dimension);
			} else if (xPos != 0) {
				Color[] colors = getBorderColors(origImg, true);
				g.setColor(colors[0]);
				g.fillRect(0, 0, xPos, dimension);
				g.setColor(colors[1]);
				g.fillRect(dimension - xPos - 1, 0, dimension, dimension);
			}
			g.drawImage(origImg, xPos, yPos, null);
			g.dispose();

			for (int thumbSize : THUMBNAIL_SIZES) {
				java.awt.Image resizedImage = thumbImg.getScaledInstance(
						thumbSize, thumbSize, java.awt.Image.SCALE_SMOOTH);
				BufferedImage resizedBufferedImage = new BufferedImage(
						thumbSize, thumbSize, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = resizedBufferedImage.createGraphics();
				g2d.drawImage(resizedImage, 0, 0, null);
				g2d.dispose();

				// save specific size of image
				bos = new ByteArrayOutputStream();
				ImageIO.write(resizedBufferedImage, "png", bos);

				images.add(bos.toByteArray());
			}
			return images;

		} catch (IllegalArgumentException rasterBankdsError) {
			// Executes when "Numbers of source Raster bands and source color
			// space components
			// do not match" exception happens
			byte[] unResizedLogo = getCompanyLogo(imageUrl);
			images.add(unResizedLogo);
			images.add(unResizedLogo);
			return images;
		} catch (Exception error) {
			System.out.println("[failed to resize company logos, using default image instead]");
			// error.printStackTrace();
			String defaultImageURL = "https://dbr2dggbe4ycd.cloudfront.net/company/default_150.png";
			byte[] unResizedLogo = getCompanyLogo(defaultImageURL);
			images.add(unResizedLogo);
			images.add(unResizedLogo);
			return images;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception error) {
					// ignore quietly
				}
			}
		}
	}

	// Returns an array of 2 colors. One to extend on top/left, and 1 to extent
	// on bottom/right
	private Color[] getBorderColors(BufferedImage image, boolean sides) {
		Color[] colors = new Color[2];
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		int height = image.getHeight();
		int width = image.getWidth();
		int length = sides ? height : width;
		int p2 = sides ? width - 1 : height - 1;
		Integer rgb = Integer.MAX_VALUE;
		int commonColor = 0;
		int numberOfOccurence = 0;
		int total = 0;
		int row = 0;
		while (row < 10) {
			for (int i = 0; i < length; i++) {
				rgb = sides ? image.getRGB(row, i) : image.getRGB(i, row);
				Integer counter = map.get(rgb);
				if (counter == null)
					counter = 0;
				counter++;
				map.put(rgb, counter);
			}
			commonColor = 0;
			numberOfOccurence = 0;
			total = 0;
			for (int color : map.keySet()) {
				int tempNumberOfOccurences = map.get(color);
				total += tempNumberOfOccurences;
				if (tempNumberOfOccurences > numberOfOccurence) {
					numberOfOccurence = tempNumberOfOccurences;
					commonColor = color;
				}
			}
			if ((double) numberOfOccurence / (double) total > .75 && row > 4)
				break;
			row++;
		}
		colors[0] = ((commonColor & 0x00FFFFFF) == 0) ? Color.WHITE
				: new Color(commonColor);

		map.clear();
		row = p2;
		while (row > p2 - 10) {
			for (int i = 0; i < length; i++) {

				rgb = sides ? image.getRGB(row, i) : image.getRGB(i, row);
				Integer counter = map.get(rgb);
				if (counter == null)
					counter = 0;
				counter++;
				map.put(rgb, counter);
			}
			commonColor = 0;
			numberOfOccurence = 0;
			total = 0;
			for (int color : map.keySet()) {
				int tempNumberOfOccurences = map.get(color);
				total += tempNumberOfOccurences;
				if (tempNumberOfOccurences > numberOfOccurence) {
					numberOfOccurence = tempNumberOfOccurences;
					commonColor = color;
				}
			}
			if ((double) numberOfOccurence / (double) total > .75
					&& row < p2 - 5)
				break;
			row--;
		}
		colors[1] = (((commonColor & 0x00FFFFFF) == 0)) ? Color.WHITE
				: new Color(commonColor);
		Color cornerColor = null;
		if (colors[0] != colors[1]
				&& (cornerColor = getCornerColor(image)) != null) {
			colors[0] = cornerColor;
			colors[1] = cornerColor;
		}

		return colors;
	}

	// Gets corner color, if all 4 corners have same color
	private Color getCornerColor(BufferedImage image) {
		int height = image.getHeight();
		int width = image.getWidth();
		Integer rgb1 = image.getRGB(0, 0);
		Integer rgb2 = image.getRGB(0, height - 1);
		Integer rgb3 = image.getRGB(width - 1, 0);
		Integer rgb4 = image.getRGB(width - 1, height - 1);
		return ((rgb1 == rgb2) && (rgb3 == rgb4) && (rgb2 == rgb3)) ? (rgb1 & 0x00FFFFFF) == rgb1
				&& image.getType() == BufferedImage.TYPE_INT_ARGB ? Color.WHITE
				: new Color(rgb1) : getCornerColorFromRGB(rgb1, rgb2, rgb3,
				rgb4);
	}

	// If colors are very close we will return an average of the RGB components
	private Color getCornerColorFromRGB(int rgb1, int rgb2, int rgb3, int rgb4) {
		Color color1 = new Color(rgb1);
		Color color2 = new Color(rgb2);
		Color color3 = new Color(rgb3);
		Color color4 = new Color(rgb4);
		int redMin = Math.min(
				color1.getRed(),
				Math.min(color2.getRed(),
						Math.min(color3.getRed(), color4.getRed())));
		int redMax = Math.max(
				color1.getRed(),
				Math.max(color2.getRed(),
						Math.max(color3.getRed(), color4.getRed())));
		int greenMin = Math.min(
				color1.getGreen(),
				Math.min(color2.getGreen(),
						Math.min(color3.getGreen(), color4.getGreen())));
		int greenMax = Math.max(
				color1.getGreen(),
				Math.max(color2.getGreen(),
						Math.max(color3.getGreen(), color4.getGreen())));
		int blueMin = Math.min(
				color1.getBlue(),
				Math.min(color2.getBlue(),
						Math.min(color3.getBlue(), color4.getBlue())));
		int blueMax = Math.max(
				color1.getBlue(),
				Math.max(color2.getBlue(),
						Math.max(color3.getBlue(), color4.getBlue())));
		if (redMax - redMin < 4 && blueMax - blueMin < 4
				&& greenMax - greenMin < 4)
			return new Color((redMax + redMin) / 2, (greenMax + greenMin) / 2,
					(blueMax + blueMin) / 2);
		else
			return null;
	}

	private byte[] getCompanyLogo(String imageURL) {
		if (imageURL.length() == 0) {
			imageURL = "https://dbr2dggbe4ycd.cloudfront.net/company/default_150.png";
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = null;
		try {
			URL url = new URL(imageURL);
			is = url.openStream();
			byte[] byteChunk = new byte[8192];
			int n;
			while ((n = is.read(byteChunk)) > 0) {
				baos.write(byteChunk, 0, n);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return baos.toByteArray();
	}

	/*
	 * public static void main(String[] args) { final String imageUrl =
	 * "http://www.crunchbase.com/assets/images/resized/0000/4561/4561v1-max-150x150.png"
	 * ; ImageProcessor test = new ImageProcessor(); byte[] iconImage =
	 * test.processImage(imageUrl); System.out.println(iconImage.length); }
	 */

}
