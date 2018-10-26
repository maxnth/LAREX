package com.web.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.opencv.core.MatOfPoint;

import com.web.model.Book;
import com.web.model.BookSettings;
import com.web.model.PageSegmentation;
import com.web.model.Point;
import com.web.model.Polygon;

import larex.geometry.positions.PriorityPosition;
import larex.geometry.positions.RelativePosition;
import larex.geometry.regions.Region;
import larex.geometry.regions.RegionManager;
import larex.geometry.regions.RegionSegment;
import larex.geometry.regions.type.RegionType;
import larex.segmentation.parameters.DEFAULT_Parameters;
import larex.segmentation.parameters.ImageSegType;
import larex.segmentation.parameters.Parameters;

/**
 * Helper Class to translate Larex Objects to Web Objects
 *
 */
public class LarexWebTranslator {

	public static BookSettings translateParameters(Parameters parameters, Book book) {
		BookSettings settings = new BookSettings(book);

		Map<String, Integer> settingParameters = settings.getParameters();
		settingParameters.put("binarythreash", parameters.getBinaryThresh());
		settingParameters.put("textdilationX", parameters.getTextDilationX());
		settingParameters.put("textdilationY", parameters.getTextDilationY());
		settingParameters.put("imagedilationX", parameters.getImageRemovalDilationX());
		settingParameters.put("imagedilationY", parameters.getImageRemovalDilationY());

		settings.setCombine(parameters.isCombineImages());
		settings.setImageSegType(parameters.getImageSegType());

		RegionManager regionManager = parameters.getRegionManager();
		for (Region region : regionManager.getRegions()) {
			RegionType regionType = region.getType();
			int minSize = region.getMinSize();
			int maxOccurances = region.getMaxOccurances();
			PriorityPosition priorityPosition = region.getPriorityPosition();
			com.web.model.Region guiRegion = new com.web.model.Region(regionType, minSize, maxOccurances,
					priorityPosition);

			int regionCount = 0;
			for (RelativePosition position : region.getPositions()) {
				LinkedList<Point> points = new LinkedList<Point>();
				points.add(new Point(position.getTopLeftXPercentage(), position.getTopLeftYPercentage()));
				points.add(new Point(position.getBottomRightXPercentage(), position.getTopLeftYPercentage()));
				points.add(new Point(position.getBottomRightXPercentage(), position.getBottomRightYPercentage()));
				points.add(new Point(position.getTopLeftXPercentage(), position.getBottomRightYPercentage()));

				String id = regionType.toString() + regionCount;
				guiRegion.addPolygon(new Polygon(id, regionType, points, true));
				regionCount++;
			}

			settings.addRegion(guiRegion);
		}
		return settings;
	}

	public static BookSettings getDefaultSettings(Book book) {
		BookSettings settings = new BookSettings(book);

		Map<String, Integer> settingParameters = settings.getParameters();
		settingParameters.put("binarythreash", DEFAULT_Parameters.getBinaryThreshDefault());
		settingParameters.put("textdilationX", DEFAULT_Parameters.getTextRemovalDilationXDefault());
		settingParameters.put("textdilationY", DEFAULT_Parameters.getTextRemovalDilationYDefault());
		settingParameters.put("imagedilationX", DEFAULT_Parameters.getImageRemovalDilationXDefault());
		settingParameters.put("imagedilationY", DEFAULT_Parameters.getImageRemovalDilationYDefault());

		settings.setCombine(true);
		settings.setImageSegType(ImageSegType.ROTATED_RECT);
		RegionManager regionManager = new RegionManager();
		for (Region region : regionManager.getRegions()) {

			RegionType regionType = region.getType();
			int minSize = region.getMinSize();
			int maxOccurances = region.getMaxOccurances();
			PriorityPosition priorityPosition = region.getPriorityPosition();
			com.web.model.Region guiRegion = new com.web.model.Region(regionType, minSize, maxOccurances,
					priorityPosition);

			int regionCount = 0;
			for (RelativePosition position : region.getPositions()) {
				LinkedList<Point> points = new LinkedList<Point>();
				points.add(new Point(position.getTopLeftXPercentage(), position.getTopLeftYPercentage()));
				points.add(new Point(position.getBottomRightXPercentage(), position.getTopLeftYPercentage()));
				points.add(new Point(position.getBottomRightXPercentage(), position.getBottomRightYPercentage()));
				points.add(new Point(position.getTopLeftXPercentage(), position.getBottomRightYPercentage()));

				String id = regionType.toString() + regionCount;
				guiRegion.addPolygon(new Polygon(id, regionType, points, true));
				regionCount++;
			}

			settings.addRegion(guiRegion);
		}
		return settings;
	}

	public static List<Point> translatePointsToContour(MatOfPoint mat) {
		LinkedList<Point> points = new LinkedList<Point>();
		for (org.opencv.core.Point regionPoint : mat.toList()) {
			points.add(new Point(regionPoint.x, regionPoint.y));
		}

		return points;
	}

	public static Polygon translatePointsToSegment(MatOfPoint mat, String id, RegionType type) {
		LinkedList<Point> points = new LinkedList<Point>();
		for (org.opencv.core.Point regionPoint : mat.toList()) {
			points.add(new Point(regionPoint.x, regionPoint.y));
		}

		Polygon segment = new Polygon(id, type, points, false);
		return segment;
	}

	public static Polygon translateResultRegionToSegment(RegionSegment region) {
		return translatePointsToSegment(region.getPoints(), region.getId(), region.getType());
	}

	public static PageSegmentation translateResultRegionsToSegmentation(String fileName, int width, int height,
			ArrayList<RegionSegment> regions, int pageid) {
		Map<String, Polygon> segments = new HashMap<String, Polygon>();

		for (RegionSegment region : regions) {
			Polygon segment = translateResultRegionToSegment(region);
			segments.put(segment.getId(), segment);
		}
		return new PageSegmentation(fileName, width, height, pageid, segments);
	}
}