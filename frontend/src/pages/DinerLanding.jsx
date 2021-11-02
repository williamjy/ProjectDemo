import React, { useState, useEffect, useContext } from "react";
import NavBar from "../components/Navbar";
import { MainContainer } from "../styles/MainContainer";
import Carousel from "react-material-ui-carousel";
import { useHistory, Redirect } from "react-router";
import { StoreContext } from "../utils/store";
import { logUserOut } from "../utils/logoutHelper";
import {
  Card,
  Grid,
  CardContent,
  // CardHeader,
  CardMedia,
  Typography,
  makeStyles,
  Select,
  MenuItem,
  InputLabel,
  Box,
  FormControl,
  Divider
} from "@material-ui/core";
import Loading from "../components/Loading";
import EateryDisplay from "../components/EateryDisplay";
import RatingWithNum from "../components/RatingWithNum";
import DinerLandingImage from "../assets/DinerLandingImage.png";
import request from "../utils/request";

const useStyles = makeStyles({
  card: {
    color: "white",
    borderRadius: "0px",
    transition: "transform 0.15s ease-in-out",
    maxHeight: "250px",
    "&:hover": {
      transform: "scale3d(1.02, 1.02, 1)",
      cursor: "pointer",
    },
    position: "relative",
    margin: "1%",
  },
  stars: {
    position: "absolute",
    right: "10%",
    bottom: "5%",
  },
  overlay: {
    position: "absolute",
    bottom: "0px",
    left: "0px",
    width: "100%",
    height: "21%",
    color: "white",
    backgroundColor: "rgba(0,0,0,0.5)",
  },
  media: {
    height: 0,
    paddingTop: "80%",
  },
  dinerLandingImage: {
    objectFit: "cover",
    height: "auto",
    width: "100%",
  },
  dinerNameText: {
    position: "absolute",
    bottom: "6%",
    left: "5%",
    fontSize: "1.7vw",
    textTransform: "uppercase",
    color: "#FF855B",
    fontWeight: "bold",
    letterSpacing: "0.1em",
  },
  text: {
    fontSize: "1.7vw",
    textTransform: "uppercase",
    color: "#FF855B",
    fontWeight: "bold",
    letterSpacing: "0.1em",
    marginBottom: "1%",
  },
  bannerBox: {
    marginBottom: "40px",
    position: "relative"
  }
});

export default function DinerLanding ({ token }) {
  const [eateryList, setEateryList] = useState([]);
  const classes = useStyles();
  const history = useHistory();
  const context = useContext(StoreContext);
  const [auth, setAuth] = context.auth;
  const [isDiner, setIsDiner] = context.isDiner;
  const [name, setName] = useState("");
  const [sortBy, setSortBy] = useState("Rating");
  const [loading, setLoading] = useState(false);
  const [recommendationList, setRecommendationList] = useState([]);
  const [location, setLocation] = useState({});

  useEffect(() => {
    const getUserLocation = () => {
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition((pos) =>
          setLocation({
            longitude: pos.coords.longitude,
            latitude: pos.coords.latitude,
          })
        );
      } else {
        console.log("location not permitted");
      }
    };
    getUserLocation();
  }, []);

  useEffect(() => {
    const getEateryList = async () => {
      setLoading(true);
      let url;
      if (sortBy === "Distance") {
        url = `list/eateries?sort=${sortBy}&latitude=${location.latitude}&longitude=${location.longitude}`;
      } else {
        url = `list/eateries?sort=${sortBy}`;
      }
      const response = await request.get(url, auth);
      const responseData = await response.json();
      setLoading(false);
      if (response.status === 200) {
        console.log(responseData);
        setName(responseData.name);
        setEateryList(responseData.eateryList);
      } else if (response.status === 401) {
        logUserOut(setAuth, setIsDiner);
      }
    };
    getEateryList();
  }, [auth, setAuth, setIsDiner, sortBy]);

  useEffect(() => {
    const getRecommendationList = async () => {
      setLoading(true);
      const response = await request.get("recommendation", auth);
      const responseData = await response.json();
      setLoading(false);
      if (response.status === 200) {
        console.log(responseData);
        setRecommendationList(
          responseData.eateryList.filter((eatery) => eatery.discount !== "0%")
        );
      } else if (response.status === 401) {
        logUserOut(setAuth, setIsDiner);
      }
    };
    getRecommendationList();
  }, [auth, setAuth, setIsDiner]);

  if (auth === null) return <Redirect to="/" />;
  if (isDiner === "false") return <Redirect to="/EateryLanding" />;

  const getCuisineList = (cuisines) => {
    if (cuisines.length < 3) return cuisines.join(", ");
    const cuisineString = cuisines.slice(0, 2).join(", ") + "..";
    return cuisineString;
  };

  const getSlides = () => {
    if (!recommendationList) return;
    const splitEateryList = [];
    for (let i = 0; i < recommendationList.length; i++) {
      const subList = recommendationList.slice(i, i + 3);
      splitEateryList.push(subList);
      i = i + 2;
    }
    console.log(splitEateryList);

    return splitEateryList.map((item, key) => {
      return (
        <Grid
          container
          justify="space-between"
          alignItems="center"
          direction="row"
          key={key}
        >
          {Array.from({ length: 3 }, (x, i) => {
            return (
              <Grid item xs={4} key={i}>
                {item[i] && (
                  <Card
                    className={classes.card}
                    onClick={(e) =>
                      history.push({
                        pathname: `/EateryProfile/${item[i].name}/${item[i].id}`,
                      })
                    }
                  >
                    <CardMedia
                      className={classes.media}
                      image={item[i].profilePic}
                    />
                    <CardContent className={classes.overlay}>
                      <Grid
                        container
                        justify="space-between"
                        alignItems="flex-end"
                      >
                        <Grid item xs={6}>
                          <div>{`UP TO ${item[i].discount} OFF`}</div>
                          <Typography variant="h5">{item[i].name}</Typography>
                          <Typography variant="subtitle2">
                            {getCuisineList(item[i].cuisines)}
                          </Typography>
                        </Grid>
                        <Grid item xs={6} className={classes.stars}>
                          <RatingWithNum rating={item[i].rating} />
                        </Grid>
                      </Grid>
                    </CardContent>
                  </Card>
                )}
              </Grid>
            );
          })}
        </Grid>
      );
    });
  };

  const getEateries = () => {
    if (!eateryList) return;
    return eateryList.map((item, index) => {
      return (
        <EateryDisplay
          name={item.name}
          id={item.id}
          key={index}
          discount={item.discount}
          cuisines={item.cuisines}
          rating={item.rating}
          image={item.profilePic}
        />
      );
    });
  };
  return (
    <>
      <NavBar isDiner={isDiner} />
      <MainContainer>
        <Box py={4}>
          <Box className={classes.bannerBox}>
            <img
              className={classes.dinerLandingImage}
              alt="welcome banner with diner's username"
              src={DinerLandingImage}
            />
            <div className={classes.dinerNameText}>{name}</div>
          </Box>
          <div className={classes.text}>
            {recommendationList.length === 0
              ? ""
              : "Restaurants we think you would like"}
          </div>

          <Carousel
            fullHeightHover={false}
            navButtonsProps={{
              style: {
                opacity: "50%",
              },
            }}
            navButtonsWrapperProps={{
              style: {
                bottom: "40%",
                top: "unset",
              },
            }}
          >
            {getSlides()}
          </Carousel>
          <Box paddingTop="40px" paddingBottom="40px">
          <Divider variant="middle" />
        </Box>
        <Grid container justifyContent="space-between" alignItems="flex-end" style={{ paddingBottom: "20px" }}>
          <Grid item>
          <div className={classes.text}>
              {eateryList.length === 0
                ? ""
                : `Restaurants Sorted By ${sortBy}`}
          </div>
          </Grid>
          <Grid item>
            <FormControl
                variant="filled"
                style={{ minWidth: "100px" }}
            >
              <InputLabel>Sort By</InputLabel>
              <Select
                defaultValue={"Rating"}
                onChange={(e) => setSortBy(e.target.value)}
              >
                <MenuItem value={"Rating"}>Rating</MenuItem>
                <MenuItem selected value={"Distance"}>
                    Distance
                </MenuItem>
                <MenuItem value={"New"}>New</MenuItem>
              </Select>
            </FormControl>
            </Grid>
          </Grid>
          {getEateries()}
        </Box>
        <Loading isLoading={loading} />
      </MainContainer>
    </>
  );
}
