import React, { useState, useEffect } from "react";
import NavBar from "../components/Navbar";
import { MainContainer } from "../styles/MainContainer";
import { ButtonStyled } from "../styles/ButtonStyle";
import {
  Typography,
  Grid,
  Box,
  Card,
  Modal,
  makeStyles,
} from "@material-ui/core";
import Review from "../components/Review";
import { useLocation, Redirect, useHistory } from "react-router-dom";
import { StoreContext } from "../utils/store";
import Carousel from "react-material-ui-carousel";
import EditCreateReview from "../components/EditCreateReview";
import ConfirmModal from "../components/ConfirmModal";
import { logUserOut } from "../utils/logoutHelper";
import { handleTimeNextDay } from "../utils/helpers";
import Loading from "../components/Loading";
import EateryDisplay from "../components/EateryDisplay";
import request from "../utils/request";

const useStyles = makeStyles({
  photo: {
    color: "black",
    transition: "transform 0.15s ease-in-out",
    "&:hover": { transform: "scale3d(1.02, 1.02, 1)", maxHeight: "none" },
    width: "150px",
    height: "150px",
    boxShadow: "0 4px 8px 0 rgba(0, 0, 0, 0.3)",
    objectFit: "contain",
    backgroundColor: "white",
  },
  photoCarousel: {
    width: "400px",
    height: "400px",
    boxShadow: "0 4px 8px 0 rgba(0, 0, 0, 0.3)",
    objectFit: "contain",
    backgroundColor: "white",
    padding: "100px",
  },
  gridContainer: {
    background: "rgba(255, 255, 255, 0.2)",
    marginTop: "10px",
    borderRadius: "10px",
  },
  subtitle: {
    borderBottom: "1px solid #FF855B",
    margin: "10px 0px",
    padding: "5px 0px",
    color: "#FF855B",
  },
  title: {
    color: "#FF855B",
    fontSize: "2em",
    padding: "10px 0px",
  },
  codeText: {
    fontWeight: "bold",
    fontSize: "20pt",
    backgroundColor: "#FFA500",
    color: "white",
    padding: "1%",
  },
});

export default function EateryProfile () {
  const [eateryDetails, setEateryDetails] = useState({});
  const location = useLocation();
  const classes = useStyles();
  const context = React.useContext(StoreContext);
  const [auth, setAuth] = context.auth;
  const [isDiner, setIsDiner] = context.isDiner;
  const eateryId = location.pathname.split("/")[3]
    ? location.pathname.split("/")[3]
    : "";
  const [openCreateReview, setOpenCreateReview] = useState(false);
  const [user, setUser] = useState({});
  const [openConfirmModal, setOpenConfirmModal] = useState(false);
  const [loading, setLoading] = useState(false);

  const [open, setOpen] = useState(false);
  const [voucherDetails, setVoucherDetails] = useState({});
  const [code, setCode] = useState("");
  const [isConfirmed, setIsConfirmed] = useState(false);
  const setAlertOptions = context.alert[1];
  const history = useHistory();

  const handleOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  useEffect(() => {
    // on page init, load the users details
    const getUser = async () => {
      const response = await request.get("diner/profile/details", auth);
      const responseData = await response.json();
      if (response.status === 200) {
        console.log(responseData);
        setUser({
          name: responseData.name,
          email: responseData.email,
          profilePic: responseData["profile picture"],
        });
        // setEateryList(responseData.eateryList);
      } else if (response.status === 401) {
        logUserOut(setAuth, setIsDiner);
      }
    };
    if (isDiner !== "false") {
      getUser();
    }
  }, [auth, isDiner, setAuth, setIsDiner]);

  const getEateryDetails = async () => {
    setLoading(true);
    const response = await request.get(
      `eatery/profile/details?id=${eateryId}`,
      auth
    );
    setLoading(false);
    const responseData = await response.json();
    if (response.status === 200) {
      console.log(responseData);
      if (responseData.vouchers.length > 0) {
        responseData.vouchers = responseData.vouchers.filter((v) => v.isActive);
      }
      setEateryDetails(responseData);
    } else if (response.status === 401) {
      logUserOut(setAuth, setIsDiner);
    } else {
      // TODO
      console.log(responseData);
    }
  };

  useEffect(() => {
    getEateryDetails();
  }, [auth, eateryId]);
  if (auth === null) return <Redirect to="/" />;

  const getReviews = () => {
    if (!eateryDetails.reviews) return;
    if (eateryDetails.reviews.length === 0) {
      return <div>No reviews have been made.</div>;
    }
    console.log(eateryDetails.reviews);
    return eateryDetails.reviews.map((item, key) => {
      return (
        <Review
          id={item.reviewId}
          onEateryProfile={true}
          profilePic={item.profilePic}
          username={item.name}
          eateryName={item.eateryName}
          review={item.message}
          rating={item.rating}
          isOwner={item.isOwner}
          eateryId={item.eateryId}
          images={item.reviewPhotos ? item.reviewPhotos : []}
          key={key}
          refreshList={() => getEateryDetails()}
        ></Review>
      );
    });
  };

  const getImages = () => {
    if (!eateryDetails.menuPhotos) return;
    if (eateryDetails.menuPhotos.length === 0) {
      return <div>no images currently</div>;
    }
    return eateryDetails.menuPhotos.map((item, key) => {
      return (
        <img
          src={item}
          alt="menu photos"
          key={key}
          className={classes.photoCarousel}
        />
      );
    });
  };

  const getSingleImage = () => {
    if (!eateryDetails.menuPhotos) return;
    if (eateryDetails.menuPhotos.length === 0) {
      return;
    }
    return (
      <img
        className={classes.photo}
        src={eateryDetails.menuPhotos[0]}
        alt="eatery menu"
        onClick={handleOpen}
      />
    );
  };

  const getNumberOfImages = () => {
    if (!eateryDetails.menuPhotos) return;
    if (eateryDetails.menuPhotos.length === 0) {
      return <Box pt={1} pb={2}>No menu photos are currently available.</Box>;
    }
  };

  const handleBooking = async () => {
    const response = await request.post(
      `diner/book?id=${voucherDetails.voucherID}`,
      {},
      auth
    );
    const responseData = await response.json();
    if (response.status === 200) {
      console.log(responseData);
      setCode(responseData.data.code);
      setIsConfirmed(true);
      setAlertOptions({
        showAlert: true,
        variant: "success",
        message: responseData.message,
      });
    } else {
      setAlertOptions({
        showAlert: true,
        variant: "error",
        message: responseData.message,
      });
      setOpenConfirmModal(false);
    }
  };

  const handleVoucher = (startTime, endTime, discount, id, date) => {
    setOpenConfirmModal(true);
    setVoucherDetails({
      startTime: startTime,
      endTime: endTime,
      discount: discount,
      date: date,
      voucherID: id,
    });
  };

  const handleCloseModal = () => {
    setOpenConfirmModal(false);
    setIsConfirmed(false);
    getEateryDetails();
  };

  const getVouchers = () => {
    if (!eateryDetails.vouchers) return;
    if (eateryDetails.vouchers.length === 0) {
      return <div> There are no vouchers currently available. </div>;
    }
    return eateryDetails.vouchers.map((item, key) => {
      return (
        ((item.nextUpdate !== "Deleted" && item.isRecurring === true) ||
          item.isRecurring === false) &&
        item.quantity !== 0 && (
          <Card
            style={{
              paddingTop: "25px",
              paddingBottom: "25px",
              borderRadius: "0px",
            }}
            key={key}
          >
            <Grid container justify="space-around" alignItems="center">
              <Grid item xs={4} style={{ alignItems: "center" }}>
                <ButtonStyled
                  variant="contained"
                  color="primary"
                  style={{ display: "block", width: "15vw" }}
                  disabled={
                    !!(item.disableButton === true || item.quantity === 0)
                  }
                  onClick={() =>
                    handleVoucher(
                      item.startTime,
                      item.endTime,
                      item.discount,
                      item.id,
                      item.date
                    )
                  }
                >
                  {`${item.discount}% OFF - ${item.eatingStyle}`}
                </ButtonStyled>
              </Grid>
              <Grid item xs={4}>
                <Box style={{ margin: "10px" }}>
                  {`${item.quantity} Voucher${
                    item.quantity === 1 ? "" : "s"
                  } Left`}
                </Box>
                <Box style={{ margin: "10px" }}>{`${item.date}`}</Box>
                <Box style={{ margin: "10px" }}>
                  {`Valid ${item.startTime} - ${handleTimeNextDay(
                    item.endTime
                  )}`}
                </Box>
              </Grid>
            </Grid>
          </Card>
        )
      );
    });
  };

  return (
    <>
      <NavBar isDiner={isDiner} />
      <MainContainer>
        <EateryDisplay
          onProfile={true}
          name={eateryDetails.name}
          cuisines={eateryDetails.cuisines}
          rating={eateryDetails.rating}
          image={eateryDetails.profilePic}
          address={eateryDetails.address}
        ></EateryDisplay>
        <Grid container spacing={5} className={classes.gridContainer}>
          <Grid item xs={6}>
            <Typography variant="h5" className={classes.subtitle}>
              Menu Photos
            </Typography>
            <Box>{getSingleImage()}</Box>
            <Box>{getNumberOfImages()}</Box>
            <Typography className={classes.subtitle} variant="h5">
              Reviews
            </Typography>
            <ButtonStyled
              variant="contained"
              color="primary"
              onClick={() => setOpenCreateReview(true)}
              disabled={isDiner !== "true"}
            >
              Write a Review
            </ButtonStyled>
            <Box>{getReviews()}</Box>
            <Loading isLoading={loading} />
          </Grid>
          <Grid item xs={6}>
            <Box className={classes.title}>Discounts</Box>
            {getVouchers()}
            <div>
              <Modal
                style={{
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                }}
                open={open}
                onClose={handleClose}
              >
                {
                  <div
                    style={{
                      top: "25%",
                      margin: "auto",
                      outline: "none",
                    }}
                  >
                    <Carousel
                      navButtonsProps={{
                        style: {
                          opacity: "50%",
                        },
                      }}
                      navButtonsAlwaysVisible={true}
                      autoPlay={false}
                    >
                      {getImages()}
                    </Carousel>
                  </div>
                }
              </Modal>
            </div>
            <Loading isLoading={loading} />
          </Grid>
        </Grid>
        <EditCreateReview
          id={-1}
          eateryId={parseInt(eateryId)}
          open={openCreateReview}
          setOpen={setOpenCreateReview}
          username={user.name}
          profilePic={user.profilePic}
          reviewTextState={["", null]}
          ratingState={["", null]}
          reviewImagesState={[[], null]}
          isEdit={false}
          refreshList={() => getEateryDetails()}
        />
        {openConfirmModal && (
          <ConfirmModal
            open={openConfirmModal}
            handleClose={handleCloseModal}
            // eateryId={eateryId}
            title={!isConfirmed
              ? "Confirmation"
              : "Discount Booked!"}
            message={!isConfirmed
              ? <div>
                  <Box pb={2}display="flex" justifyContent="center">
                    <b>Purchase for {voucherDetails.discount}% off at {eateryDetails.name}</b>:
                  </Box>
                  <div>
                    Valid for use between {voucherDetails.startTime} -{" "}
                    {handleTimeNextDay(voucherDetails.endTime)} on{" "}
                    {voucherDetails.date}
                  </div>
                </div>
              : <div>
                <Box pb={2}display="flex" justifyContent="center">
                  Thank you for purchasing a {voucherDetails.discount}% off
                  voucher at {eateryDetails.name}!
                </Box>
                <Box
                  pb={2}
                  display="flex"
                  justifyContent="center"
                  style={{ fontWeight: "bold" }}
                >
                  Present this code at the restaurant to redeem:
                </Box>
                <Box pb={2} display="flex" justifyContent="center">
                  <div className={classes.codeText}>{code}</div>
                </Box>
                <Box display="flex" justifyContent="center">
                  Valid for use between {voucherDetails.startTime} -{" "}
                  {handleTimeNextDay(voucherDetails.endTime)} on{" "}
                  {voucherDetails.date}
                </Box>
              </div>
            }
            denyText={isConfirmed ? "View Vouchers" : "Cancel"}
            handleDeny={
              isConfirmed ? () => history.push("/DinerVouchers") : null
            }
            handleConfirm={
              isConfirmed ? () => handleCloseModal() : () => handleBooking()
            }
            confirmText={isConfirmed ? "Ok" : "Confirm"}
          ></ConfirmModal>
        )}
      </MainContainer>
    </>
  );
}
