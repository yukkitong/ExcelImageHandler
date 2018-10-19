package kr.co.uniess.kto.batch.repository;

import lombok.Data;

@Data
public class Image {
  private String imgId;
  private String cotId;
  private String imageDescription;
  private String url;
  private boolean thumbnail;
}