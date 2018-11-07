package me.chrishughes.meetupeventslight.model;

import com.squareup.moshi.Json;

public class Rsvp {

  @Json(name = "member")
  private RsvpMember member;

  @Json(name = "member_photo")
  private MemberPhoto memberPhoto;

  public RsvpMember getMember() {
    return member;
  }

  public MemberPhoto getMemberPhoto() {
    return memberPhoto;
  }

  static public class MemberPhoto {

    @Json(name = "photo_link")
    private String photoLink;

    public String getPhotoLink() {
      return photoLink;
    }
  }

  static public class RsvpMember {

    @Json(name = "name")
    private String name;

    public String getName() {
      return name;
    }
  }

}
