import React from "react";
import { Grid } from "@material-ui/core";
import StarRating from "../components/StarRating";

export default function RatingWithNum ({ rating }) {
  return (
    <>
      <Grid item container>
        <Grid item>
          <StarRating rating={parseFloat(rating)} />
        </Grid>
        {/* aligns the rating text with the stars */}
        <Grid
          item
          style={{
            marginLeft: "10px",
            marginTop: "4px",
          }}
        >
          {rating}
        </Grid>
      </Grid>
    </>
  );
}
