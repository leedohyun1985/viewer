package com.doh.viewer.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

@Controller
public class IndexController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${viewer.base.path}")
	private String basePath;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(HttpServletRequest httpServletRequest, Locale locale) {
		return "index";
	}

	@RequestMapping(value = "/imageviewer", method = RequestMethod.GET)
	public String imageviewer(HttpServletRequest httpServletRequest, Locale locale) {
		return "imageviewer";
	}

	@RequestMapping(value = "/imagefolder", method = RequestMethod.GET)
	public String imagefolder(HttpServletRequest httpServletRequest, Locale locale) {
		return "imagefolder";
	}

	@RequestMapping(value = "/pdfviewer", method = RequestMethod.GET)
	public String pdfviewer(HttpServletRequest httpServletRequest, Locale locale) {
		return "pdfviewer";
	}

	@GetMapping(value = "/pdf/**", produces = MediaType.APPLICATION_PDF_VALUE)
	public @ResponseBody byte[] getPdfFile(HttpServletRequest httpServletRequest) throws IOException {
		String filePathName = (String) httpServletRequest
				.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		StringBuilder resourcePath = new StringBuilder();
		resourcePath.append(basePath).append(filePathName);
		InputStream inputStream = getClass().getResourceAsStream(resourcePath.toString());
		return IOUtils.toByteArray(inputStream);
	}

	@GetMapping(value = "/image/**")
	public @ResponseBody ResponseEntity<byte[]> getImageFile(HttpServletRequest httpServletRequest) throws IOException {
		String filePathName = (String) httpServletRequest
				.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		logger.info("basePath : " + basePath);
		logger.info("filePathName : " + filePathName);
		StringBuilder resourcePath = new StringBuilder();
		resourcePath.append(basePath).append(filePathName);
		logger.info("resourcePath : " + resourcePath.toString());
		String fileFormat = filePathName.split("\\.")[filePathName.split("\\.").length - 1];
		HttpHeaders header = new HttpHeaders();
		switch (fileFormat.toLowerCase()) {
		case "jpg":
		case "jpeg":
			header.setContentType(MediaType.IMAGE_JPEG);
			break;
		case "gif":
			header.setContentType(MediaType.IMAGE_GIF);
			break;
		case "png":
			header.setContentType(MediaType.IMAGE_PNG);
			break;
		default:
			return new ResponseEntity<byte[]>(HttpStatus.NOT_ACCEPTABLE);
		}

		return new ResponseEntity<byte[]>(IOUtils.toByteArray(new FileInputStream(new File(resourcePath.toString()))),
				header, HttpStatus.CREATED);
	}
}
