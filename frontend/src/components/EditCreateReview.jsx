import React, { useState, useContext } from "react";
import {
  Dialog,
  DialogContent,
  Box,
  DialogActions,
  makeStyles,
} from "@material-ui/core";
import { ProfilePhoto } from "../styles/ProfilePhoto";
import UploadPhotos from "./UploadPhotos";
import StarRating from "./StarRating";
import { StoreContext } from "../utils/store";
import CloseIcon from "@material-ui/icons/Close";
import { ModalButton } from "../styles/ModalButton";
import { TextFieldStyled } from "../styles/TextFieldStyled";
import { CloseIconStyled } from "../styles/CloseIconStyled";
import { DialogTitleStyled } from "../styles/DialogTitleStyled";
import { Transition } from "../utils/helpers";
import request from "../utils/request";

const useStyles = makeStyles({
  paper: {
    minWidth: "30vw",
    minHeight: "60vh",
    animationDuration: "0.3s",
    animationTimingFunction: "ease-in-out",
  },
  helperText: {
    textAlign: "right",
  },
});

export default function EditCreateReview ({
  id,
  eateryId,
  open,
  setOpen,
  username,
  profilePic,
  reviewImagesState,
  reviewTextState,
  ratingState,
  isEdit,
  refreshList,
}) {
  const context = useContext(StoreContext);
  const setAlertOptions = context.alert[1];
  const token = context.auth[0];
  const classes = useStyles();
  // Will use this images as the array of strings that will be the final images that get saved
  const [images, setImages] = useState(reviewImagesState[0]);
  const [previewImages, setPreviewImages] = useState(reviewImagesState[0]);
  const [reviewText, setReviewText] = useState(reviewTextState[0]);
  const [rating, setRating] = useState(ratingState[0]);

  const handleUpdateReview = async () => {
    console.log(
      "Make the API call here that will save this particular review for a particular restaurant"
    );
    const payload = {
      id: id,
      eateryId: eateryId,
      rating: rating,
      message: reviewText,
      reviewPhotos: images,
    };
    const response = await request.post("diner/editreview", payload, token);
    const responseData = await response.json();
    if (response.status === 200) {
      refreshList();
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
    }
    setOpen(false);
    reviewImagesState[1](images);
    reviewTextState[1](reviewText);
    ratingState[1](rating);
  };

  const handleCreateReview = async () => {
    const payload = {
      eateryId: eateryId,
      rating: rating,
      message: reviewText,
      reviewPhotos: images,
    };
    const response = await request.post("diner/createreview", payload, token);
    const responseData = await response.json();
    if (response.status === 200) {
      refreshList();
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
    }
    setOpen(false);
  };

  return (
    <>
      <Dialog
        aria-labelledby="customized-dialog-title"
        open={open}
        classes={{ paper: classes.paper }}
        onClose={() => setOpen(false)}
        TransitionComponent={Transition}
        keepMounted
      >
        <DialogTitleStyled aria-label="customized-dialog-title">
          {isEdit ? "Edit Review" : "Create Review"}
        </DialogTitleStyled>
        <CloseIconStyled aria-label="close" onClick={() => setOpen(false)}>
          <CloseIcon />
        </CloseIconStyled>
        <DialogContent dividers>
          <Box pt={1} display="flex">
            <ProfilePhoto size={70} src={profilePic}></ProfilePhoto>
            <h3>{username}</h3>
          </Box>
          <Box pt={1}>
            <StarRating
              rating={rating}
              isEditable={true}
              setRating={setRating}
            ></StarRating>
          </Box>
          <UploadPhotos
            setImages={setImages}
            setPreviewImages={setPreviewImages}
            previewImages={previewImages}
            uploadDescription={"Upload Review Photos"}
          />
          <Box pt={2}>
            <TextFieldStyled
              aria-label="outlined-basic"
              label="Let us know what you think..."
              onChange={(e) => setReviewText(e.target.value)}
              value={reviewText}
              variant="outlined"
              multiline
              rows={3}
              fullWidth
              inputProps={{ maxLength: 280 }}
              helperText={`${reviewText.length}/${280}`}
              FormHelperTextProps={{
                className: classes.helperText,
              }}
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <ModalButton
            autoFocus
            onClick={() => {
              setOpen(false);
              setRating(ratingState[0]);
              setReviewText(reviewTextState[0]);
              setImages(reviewImagesState[0]);
            }}
          >
            Cancel
          </ModalButton>
          <ModalButton
            autoFocus
            onClick={isEdit ? handleUpdateReview : handleCreateReview}
          >
            {isEdit ? "Save changes" : "Create review"}
          </ModalButton>
        </DialogActions>
      </Dialog>
    </>
  );
}
