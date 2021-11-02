import React, { useState, useContext } from "react";
import DeleteIcon from "@material-ui/icons/Delete";
import StarRating from "../components/StarRating";
import IconButton from "@material-ui/core/IconButton";
import { ProfilePhoto } from "../styles/ProfilePhoto";
import { Box, Divider, makeStyles } from "@material-ui/core";
import EditIcon from "@material-ui/icons/Edit";
import ConfirmModal from "./ConfirmModal";
import EditCreateReview from "../components/EditCreateReview";
import { StoreContext } from "../utils/store";
import Carousel from "react-material-ui-carousel";
import { useHistory } from "react-router-dom";
import { ButtonStyled } from "../styles/ButtonStyle";
import { ReviewInfoBox } from "../styles/ReviewInfoBox";
import request from "../utils/request";

const useStyles = makeStyles({
  photoCarousel: {
    justifyContent: "center",
    width: "35vw",
    height: "200px",
    objectFit: "contain",
    padding: "10px",
  },
  name: {
    fontSize: "1em",
    fontWeight: "bold",
    padding: "5px 0"
  },
  reviewContainer: {
    transition: "transform 0.15s ease-in-out",
    boxShadow: "0px 2px 5px rgb(0 0 0 / 0.1)",
    "&:hover": {
      transform: "scale3d(1.01, 1.01, 1)",
      cursor: "pointer",
      boxShadow: "0px 5px 10px rgb(0 0 0 / 0.1)",
    },
  }
});
export default function Review ({
  id,
  eateryId,
  username,
  profilePic,
  eateryName,
  review,
  rating,
  images,
  onEateryProfile,
  isOwner,
  refreshList,
}) {
  const context = useContext(StoreContext);
  const setAlertOptions = context.alert[1];
  const token = context.auth[0];
  const classes = useStyles();
  const [openEditCreateReview, setOpenEditCreateReview] = useState(false);
  const [openDeleteModal, setDeleteModal] = useState(false);
  const [editCreateReview, setEditCreateReview] = useState(review);
  const [editRating, setEditRating] = useState(rating);
  const [editCreateReviewImages, setEditCreateReviewImages] = useState(images);
  const handleCloseModal = () => setDeleteModal(false);
  const history = useHistory();

  const handleDelete = async (token, id, eateryId) => {
    console.log("This will be called when a review is deleted");
    console.log(token);
    console.log(id);
    const payload = {
      id: id,
      eateryId: eateryId,
    };
    const response = await request.delete("diner/removereview", payload, token);
    const responseData = await response.json();
    if (response.status === 200) {
      setAlertOptions({
        showAlert: true,
        variant: "success",
        message: responseData.message,
      });
      refreshList();
    } else {
      setAlertOptions({
        showAlert: true,
        variant: "error",
        message: responseData.message,
      });
    }
    setDeleteModal(false);
  };

  const handleEatery = (eateryId) => {
    history.push({
      pathname: `/EateryProfile/${eateryName}/${eateryId}`,
    });
  };

  return (
    <>
      <Box
        display="flex"
        flexDirection="column"
        border="2px solid #ffcc94"
        borderRadius="20px"
        padding="10px"
        bgcolor="white"
        minWidth={onEateryProfile ? "90%" : "65vw"}
        maxWidth="70vw"
        margin="10px 0"
      >
        <ReviewInfoBox>
          <Box display="flex" flexDirection="column">
            <ProfilePhoto
              hover={false}
              size={50}
              src={profilePic}
            ></ProfilePhoto>
            <StarRating rating={editRating}></StarRating>
          </Box>
          <div style={{ margin: "0px 5%", flexGrow: 1 }}>
            <Box className={classes.name}>
              {onEateryProfile ? username : eateryName}
            </Box>
            <Box>{editCreateReview}</Box>
          </div>
          <Box display="flex" flexDirection="column" justifyContent="center">
            <Box display="flex" justifyContent="center">
              {!onEateryProfile && (
                <ButtonStyled
                  variant="contained"
                  color="primary"
                  onClick={() => handleEatery(eateryId)}
                >
                  View Restaurant
                </ButtonStyled>
              )}
              {/* TODO FIX THIS BELOW */}
              {isOwner && (
                <IconButton onClick={() => setOpenEditCreateReview(true)}>
                  <EditIcon fontSize="large" />
                </IconButton>
              )}
              {isOwner && (
                <IconButton onClick={() => setDeleteModal(true)}>
                  <DeleteIcon fontSize="large" />
                </IconButton>
              )}
            </Box>
          </Box>
        </ReviewInfoBox>
        {editCreateReviewImages.length !== 0 && (
          <Box paddingY="30px">
            <Divider variant="middle" />
          </Box>
        )}
        {editCreateReviewImages.length !== 0 && (
          <div
            style={{
              top: "25%",
              margin: "auto",
              outline: "none",
              display: "flex",
              justifyContent: "center",
            }}
          >
            <Carousel
              navButtonsProps={{
                style: {
                  opacity: "50%",
                },
              }}
              navButtonsAlwaysVisible={false}
              autoPlay={false}
            >
              {editCreateReviewImages.map((imgdata, idx) => {
                return (
                  <img
                    key={idx}
                    alt="review photos"
                    src={imgdata}
                    className={classes.photoCarousel}
                  ></img>
                );
              })}
            </Carousel>
          </div>
        )}
      </Box>
      <ConfirmModal
        open={openDeleteModal}
        handleClose={handleCloseModal}
        eateryId={eateryId}
        title={"Delete review?"}
        message={"Are you sure you want to delete this review?"}
        handleConfirm={() => handleDelete(token, id, eateryId)}
      ></ConfirmModal>
      {/* put a carousel here */}
      <EditCreateReview
        id={id}
        eateryId={eateryId}
        open={openEditCreateReview}
        setOpen={setOpenEditCreateReview}
        username={username}
        profilePic={profilePic}
        reviewTextState={[editCreateReview, setEditCreateReview]}
        ratingState={[editRating, setEditRating]}
        reviewImagesState={[editCreateReviewImages, setEditCreateReviewImages]}
        isEdit={true}
        refreshList={refreshList}
      />
    </>
  );
}
