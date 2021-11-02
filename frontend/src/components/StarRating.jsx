import React from "react";
import { Rating } from "@material-ui/lab";
import StarBorderIcon from "@material-ui/icons/StarBorder";

export default function StarRating ({ rating, isEditable = false, setRating }) {
  return (
    <Rating value={rating}
      precision={0.5}
      onChange={(event, newValue) => {
        setRating(newValue);
      }}
      emptyIcon={<StarBorderIcon style={{ color: "#FFB400" }} fontSize="inherit" />}
      readOnly={!isEditable}
    >
    </Rating>
  );
}
