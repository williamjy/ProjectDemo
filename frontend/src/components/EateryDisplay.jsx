import React from "react";
import {
  Card,
  Grid,
  CardContent,
  CardMedia,
  Typography,
  makeStyles,
} from "@material-ui/core";
import { useHistory } from "react-router-dom";
import RatingWithNum from "../components/RatingWithNum";

const useStyles = makeStyles({
  wideCard: {
    marginBottom: "50px",
    transition: "transform 0.15s ease-in-out",
    "&:hover": {
      transform: "scale3d(1.01, 1.01, 1)",
      cursor: "pointer",
      boxShadow: "0px 10px 20px rgb(0 0 0 / 0.2)",
    },
  },
  profile: {
    marginTop: "40px",
    color: "white",
    backgroundColor: "rgba(0,0,0,0.5)",
    position: "relative",
  },
  overlay: {
    position: "absolute",
    bottom: "0px",
    left: "0px",
    width: "100%",
    color: "white",
    maxHeight: "30%",
    backgroundColor: "rgba(0,0,0,0.5)",
  },
  stars: {
    position: "absolute",
    bottom: "8%",
    right: "6%",
    "@media (max-width: 600px)": {
      right: "10%",
    },
  },
});

export default function EateryDisplay ({
  name,
  id,
  discount,
  cuisines,
  rating,
  image,
  onProfile = false,
  address,
}) {
  const classes = useStyles();
  const history = useHistory();

  const handleClick = () => {
    localStorage.setItem("searchTerm", "");
    if (!onProfile) {
      history.push({
        pathname: `/EateryProfile/${name}/${id}`,
      });
    }
  };
  return (
    <Card
      className={onProfile ? classes.profile : classes.wideCard}
      onClick={() => handleClick()}
    >
      <CardMedia
        style={{
          height: "200px",
        }}
        image={image}
      />
      <CardContent className={onProfile && classes.overlay}>
        <Grid container justify="space-between" alignItems="flex-end">
          <Grid item xs={10}>
            <div>{!onProfile && `UP TO  ${discount} OFF`}</div>
            <Typography variant="h5">{name}</Typography>
            <Typography variant="subtitle2">
              {cuisines && cuisines.join(", ")}
            </Typography>
            <Typography variant="subtitle2">{onProfile && address}</Typography>
          </Grid>
          <Grid item className={onProfile ? classes.stars : "none"}>
            <RatingWithNum className={classes.stars} rating={rating} />
          </Grid>
        </Grid>
      </CardContent>
    </Card>
  );
}
