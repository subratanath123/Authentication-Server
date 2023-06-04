package net.auzy.entity.user;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.redis.core.index.GeoIndexed;

@Document
@Data
public class Address {

    @Id
    private String id;

    private AddressType addressType;

    private String country;

    private String address1;

    private String state;

    private String city;

    private String zip;

    @GeoIndexed
    private Point location;

}