package com.doh.viewer.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
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
import org.springframework.ui.Model;
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

	@Value("${viewer.pdf.path}")
	private String pdfBasePath;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(HttpServletRequest httpServletRequest, Locale locale) {
		return "index";
	}

	@RequestMapping(value = "/imagefolder", method = RequestMethod.GET)
	public String imagefolder(HttpServletRequest httpServletRequest, Locale locale) {
		// 현재 이미지 폴더들을 내려보낸다.
		return "imagefolder";
	}

	@RequestMapping(value = "/imageviewer", method = RequestMethod.GET)
	public String imageviewer(HttpServletRequest httpServletRequest, Locale locale) {
		// 현재 이미지 폴더값을 가져와서 내부에 있는 이미지 파일명을 전체다 내려보내야함
		return "imageviewer";
	}

	@GetMapping(value = "/image/**")
	public @ResponseBody ResponseEntity<byte[]> getImageFile(HttpServletRequest httpServletRequest) throws IOException {
		String filePathName = (String) httpServletRequest
				.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		StringBuilder resourcePath = new StringBuilder();
		resourcePath.append(basePath).append(filePathName);
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

	@RequestMapping(value = "/pdffolder", method = RequestMethod.GET)
	public String pdffolder(HttpServletRequest httpServletRequest, Locale locale) {
		// 현재 pdf 파일들을 노출시킨다.
		return "imagefolder";
	}

	@RequestMapping(value = "/pdfviewer", method = RequestMethod.GET)
	public String pdfviewer(Model model, Locale locale) {
		File dir = new File(pdfBasePath);
		File files[] = dir.listFiles();
		String[] fileArray = new String[dir.listFiles().length];
		
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			fileArray[i] = file.getName();
			logger.info("file.getName() : " + file.getName());
		}
		
		model.addAttribute("fileArray", fileArray);
		return "pdfviewer";
	}

	@GetMapping(value = "/pdf/**")
	public @ResponseBody ResponseEntity<byte[]> getPdfFile(HttpServletRequest httpServletRequest) throws IOException {
		String filePathName = (String) httpServletRequest
				.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		StringBuilder resourcePath = new StringBuilder();
		resourcePath.append(basePath).append(filePathName);
		HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);

		return new ResponseEntity<byte[]>(IOUtils.toByteArray(new FileInputStream(new File(resourcePath.toString()))),
				header, HttpStatus.CREATED);
	}
}
