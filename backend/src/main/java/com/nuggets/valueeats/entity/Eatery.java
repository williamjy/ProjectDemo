package com.nuggets.valueeats.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
public final class Eatery extends User {
    @ElementCollection
    @CollectionTable(name = "Cuisines")
    @Column(name = "cuisine")
    private List<String> cuisines = new ArrayList<>();
    private ArrayList<String> menuPhotos;
    @JsonProperty("rating")
    @Formula("(select avg(r.rating) from review r where r.eatery_id = id)")
    private Float lazyRating;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @OneToMany
    @JoinColumn(name = "eateryId")
    private List<Review> reviews;
}
