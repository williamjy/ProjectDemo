import React, { useState, useEffect, useContext } from "react";
import NavBar from "../components/Navbar";
import { ProfilePhoto } from "../styles/ProfilePhoto";
import {
  Box,
  Divider,
  Dialog,
  DialogContent,
  DialogActions,
  Grid,
  makeStyles,
} from "@material-ui/core";
import { Label } from "../styles/Label";
import { FileUpload } from "../styles/FileUpload";
import EditIcon from "@material-ui/icons/Edit";
import AddPhotoAlternateIcon from "@material-ui/icons/AddPhotoAlternate";
import Review from "../components/Review";
import {
  validRequired,
  validEmail,
  validPassword,
  validConfirmPassword,
  handleImage,
  Transition,
} from "../utils/helpers";
import { StoreContext } from "../utils/store";
import { logUserOut } from "../utils/logoutHelper";
import { Title } from "../styles/Title";
import { ButtonStyled } from "../styles/ButtonStyle";
import { useHistory } from "react-router";
import { DialogTitleStyled } from "../styles/DialogTitleStyled";
import { ModalButton } from "../styles/ModalButton";
import { TextFieldStyled } from "../styles/TextFieldStyled";
import { CloseIconStyled } from "../styles/CloseIconStyled";
import CloseIcon from "@material-ui/icons/Close";
import { MainContainer } from "../styles/MainContainer";
import request from "../utils/request";

const useStyles = makeStyles({
  containers: {
    flexDirection: "row",
    background: "rgba(0,0,0,0.1)",
    padding: "2% 0",
    "@media (max-width: 1100px)": {
      flexDirection: "column",
      justifyContent: "center",
      alignItems: "center",
    },
  },
  dinerNameText: {
    fontSize: "3vw",
    textTransform: "uppercase",
    color: "#FF855B",
    fontWeight: "bold",
    letterSpacing: "0.1em",
  },
  overlay: {
    background: "rgba(250,255,249,0.9)",
    padding: "20px",
    minWidth: "20vw",
    "@media (max-width: 1100px)": {
      minWidth: "30vw",
    },
    "@media (max-width: 1400px)": {
      minWidth: "42vw",
    },
  },
  reviewTitle: {
    fontSize: "1.5vw",
    textTransform: "uppercase",
    color: "#96ae33",
    fontWeight: "bold",
    letterSpacing: "0.1em",
  },
});

export default function DinerProfile () {
  const context = useContext(StoreContext);
  const setAlertOptions = context.alert[1];
  const [token, setAuth] = context.auth;
  const setIsDiner = context.isDiner[1];
  const history = useHistory();
  const classes = useStyles();
  const [openProfile, setOpenProfile] = useState(false);

  const defaultState = (initialValue = "") => {
    return { value: initialValue, valid: true };
  };

  const [username, setUsername] = useState(defaultState);
  const [email, setEmail] = useState(defaultState);
  const [password, setPassword] = useState(defaultState);
  const [confirmpassword, setConfirmpassword] = useState(defaultState);
  const [tmpProfilePic, setTmpProfilePic] = useState(defaultState);
  const [user, setUser] = useState({});
  const [reviews, setReviews] = useState([]);
  const [bgImage, setBgImage] = useState("");

  const getUser = async () => {
    const response = await request.get("diner/profile/details", token);
    const responseData = await response.json();
    if (response.status === 200) {
      console.log(responseData);
      setUser({
        username: responseData.name,
        email: responseData.email,
        profilePic: responseData["profile picture"],
      });
      setBgImage(responseData["profile picture"]);
      console.log("reviews: ");
      console.log(responseData.reviews);
      setReviews(responseData.reviews);
      setUsername(defaultState(responseData.name));
      setEmail(defaultState(responseData.email));
      setTmpProfilePic(responseData["profile picture"]);
      // setEateryList(responseData.eateryList);
    } else if (response.status === 401) {
      logUserOut(setAuth, setIsDiner);
    }
  };

  useEffect(() => {
    // on page init, load the users details
    getUser();
  }, [token]);

  const handleClose = () => {
    setOpenProfile(false);
    setEmail(user.email);
    setUsername({ ...username, value: user.username });
    setEmail({ ...email, value: user.email });
    setTmpProfilePic(user.profilePic);
  };

  const saveChanges = async () => {
    console.log("changes are going");
    // Ideally this form should be validated when a form is submitted
    if (user.username.value === "") setUsername({ value: "", valid: false });
    if (user.email.value === "") setEmail({ value: "", valid: false });
    // check that all fields are valid and not empty before registering
    if (
      !username.valid ||
      !email.valid ||
      !password.valid ||
      !confirmpassword.valid ||
      username.value === "" ||
      email.value === ""
    ) {
      return;
    }
    const payload = {
      email: email.value,
      password: password.value.length !== 0 ? password.value : null,
      alias: username.value,
      profilePic: tmpProfilePic,
    };
    const response = await request.post("update/diner", payload, token);
    const responseData = await response.json();
    if (response.status === 200) {
      console.log(responseData);
      setUser({
        ...user,
        username: username.value,
        email: email.value,
        profilePic: tmpProfilePic,
      });
      setBgImage(tmpProfilePic);
      setOpenProfile(false);
      setAlertOptions({
        showAlert: true,
        variant: "success",
        message: responseData.message,
      });
      getUser();
    } else if (response.status === 401) {
      logUserOut(setAuth, setIsDiner);
    } else {
      setAlertOptions({
        showAlert: true,
        variant: "error",
        message: responseData.message,
      });
    }
  };

  const getNumPhotos = () => {
    let total = 0;
    for (const review of reviews) {
      total += review.reviewPhotos.length;
    }
    return total;
  };

  return (
    <>
      <NavBar isDiner={true} />
      <MainContainer>
        <Grid
          container
          justifyContent="center"
          style={{
            backgroundImage: `url("${bgImage}")`,
            backgroundSize: "cover",
            backgroundRepeat: "no-repeat",
            backgroundPosition: "center",
            marginTop: "3%",
          }}
          direction="column"
        >
          <Grid
            item
            container
            justifyContent="center"
            className={classes.containers}
          >
            <Grid
              item
              justifyContent="center"
              align="center"
              xs={3}
              className={classes.overlay}
            >
              <ProfilePhoto size={180} src={user.profilePic} />
            </Grid>
            <Grid
              item
              justifyContent="center"
              xs={4}
              align="center"
              direction="column"
              container
              className={classes.overlay}
            >
              <Grid item container alignItems="center" justifyContent="center">
                <Grid item className={classes.dinerNameText}>
                  <Box>{user.username}</Box>
                  <Box>
                    <ButtonStyled
                      variant="contained"
                      color="primary"
                      startIcon={<EditIcon />}
                      onClick={() => setOpenProfile(true)}
                    >
                      Edit Profile
                    </ButtonStyled>
                  </Box>
                </Grid>
              </Grid>
              <Grid
                item
                justifyContent="center"
                alignItems="center"
                direction="row"
                container
                style={{ color: "black" }}
              >
                {`${reviews.length} review${
                  reviews.length === 1 ? "" : "s"
                } | ${getNumPhotos()} photo${getNumPhotos() === 1 ? "" : "s"}`}
              </Grid>
            </Grid>
          </Grid>
        </Grid>
        <Box paddingTop="20px" paddingBottom="10px">
          <Divider variant="middle" />
        </Box>
        {/* Reviews would be mapped here... */}
        <Box align="center" className={classes.reviewTitle}>
          Past Reviews
        </Box>
        <Box
          display="flex"
          flexDirection="column"
          flex="1"
          alignItems="center"
          style={{ overflowY: "auto", height: "100%" }}
        >
          {reviews.length > 0 &&
            reviews.map((r) => {
              console.log(r);
              return (
                <Review
                  key={r.reviewId}
                  id={r.reviewId}
                  eateryId={r.eateryId}
                  username={r.name}
                  profilePic={r.profilePic}
                  eateryName={r.eateryName}
                  review={r.message}
                  rating={r.rating}
                  images={r.reviewPhotos ? r.reviewPhotos : []}
                  isOwner={true}
                  onEateryProfile={false}
                  refreshList={() => getUser()}
                ></Review>
              );
            })}
          {reviews.length === 0 && (
            <Box
              display="flex"
              flexDirection="column"
              alignItems="center"
              height="70vh"
              pt={2}
            >
              <Title>No Reviews made yet...</Title>
              <ButtonStyled
                widthPercentage={50}
                onClick={() => history.push("/DinerLanding")}
              >
                Find restaurants
              </ButtonStyled>
            </Box>
          )}
        </Box>
        <Dialog
          aria-labelledby="customized-dialog-title"
          open={openProfile}
          onClose={() => setOpenProfile(false)}
          TransitionComponent={Transition}
          keepMounted
        >
          <DialogTitleStyled aria-label="customized-dialog-title">
            Update Profile
          </DialogTitleStyled>
          <CloseIconStyled
            aria-label="close"
            onClick={() => setOpenProfile(false)}
          >
            <CloseIcon />
          </CloseIconStyled>
          <DialogContent dividers>
            <Box pt={1} display="flex" alignItems="center">
              <Box>
                <Label style={{ border: "0px", padding: "0px" }}>
                  <FileUpload
                    type="file"
                    accept="image/png, image/jpg, image/jpeg"
                    onChange={(e) =>
                      handleImage(e.target.files, setTmpProfilePic)
                    }
                  />
                  <Box position="relative" p={1}>
                    <AddPhotoAlternateIcon
                      style={{
                        position: "absolute",
                        left: "65px",
                        bottom: "65px",
                        backgroundColor: "white",
                        zIndex: 5,
                      }}
                    />
                    <ProfilePhoto hover={true} size={70} src={tmpProfilePic} />
                  </Box>
                </Label>
              </Box>
              <TextFieldStyled
                label="Username"
                onChange={(e) =>
                  setUsername({ value: e.target.value, valid: true })
                }
                onBlur={() => validRequired(username, setUsername)}
                error={!username.valid}
                helperText={username.valid ? "" : "Please enter a username with at most 12 characters"}
                value={username.value}
                variant="outlined"
                fullWidth
              />
            </Box>
            <Box pt={0.5}>
              <TextFieldStyled
                label="Email Address"
                onChange={(e) =>
                  setEmail({ value: e.target.value, valid: true })
                }
                onBlur={() => validEmail(email, setEmail)}
                error={!email.valid}
                helperText={email.valid ? "" : "Please enter a valid email"}
                value={email.value}
                variant="outlined"
                fullWidth
              />
            </Box>
            <Box pt={2}>
              <TextFieldStyled
                aria-label="outlined-basic"
                label="Password"
                type="password"
                onChange={(e) =>
                  setPassword({ value: e.target.value, valid: true })
                }
                onBlur={() => validPassword(password, setPassword)}
                error={!password.valid && password.value.length !== 0}
                helperText={
                  password.valid || password.value.length === 0
                    ? ""
                    : "Please enter a valid password with 1 lowercase, 1 upper case, 1 number with at least 8 characters"
                }
                variant="outlined"
                fullWidth
              />
            </Box>
            <Box pt={2}>
              <TextFieldStyled
                aria-label="outlined-basic"
                label="Confirm Password"
                type="password"
                onChange={(e) =>
                  setConfirmpassword({
                    value: e.target.value,
                    valid: true,
                  })
                }
                onBlur={() =>
                  validConfirmPassword(
                    password,
                    confirmpassword,
                    setConfirmpassword
                  )
                }
                error={!confirmpassword.valid}
                helperText={
                  confirmpassword.valid
                    ? ""
                    : "Please make sure your passwords match"
                }
                variant="outlined"
                fullWidth
              />
            </Box>
          </DialogContent>
          <DialogActions>
            <ModalButton autoFocus onClick={handleClose} color="primary">
              Cancel
            </ModalButton>
            <ModalButton autoFocus onClick={saveChanges} color="primary">
              Save changes
            </ModalButton>
          </DialogActions>
        </Dialog>
      </MainContainer>
    </>
  );
}
