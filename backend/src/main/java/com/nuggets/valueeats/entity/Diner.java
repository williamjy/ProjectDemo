package com.nuggets.valueeats.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@Data
@ToString(callSuper = true)
@PrimaryKeyJoinColumn(name = "id")
public final class Diner extends User {
}
