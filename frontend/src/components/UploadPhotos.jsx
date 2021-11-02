import React from "react";
import { Box, IconButton } from "@material-ui/core";
import AddAPhotoIcon from "@material-ui/icons/AddAPhoto";
import { fileToDataUrl } from "../utils/helpers";
import { ImagePreview } from "../styles/ImagePreview";
import { Label } from "../styles/Label";
import { FileUpload } from "../styles/FileUpload";
import CancelIcon from "@material-ui/icons/Cancel";

export default function UploadPhotos ({ setImages, previewImages, setPreviewImages, uploadDescription }) {
  const removePhoto = (idx) => {
    const updatedImages = previewImages.filter((img, id) => id !== idx);
    setPreviewImages(updatedImages);
    setImages(updatedImages);
  };

  const handleImages = async (data) => {
    const allPromises = [];
    Array.from(data).forEach((file) => {
      allPromises.push(fileToDataUrl(file));
    });
    Promise.all(allPromises).then((urlArray) => {
      setImages((prev) => prev ? prev.concat(urlArray) : urlArray);
      setPreviewImages((prev) => prev ? prev.concat(urlArray) : urlArray);
    });
  };

  const getPreviewImages = (data) => {
    return data.map((photo, idx) => {
      return (
        <Box key={idx} position="relative" p={1}>
          <IconButton style={{ position: "absolute", left: "35px", bottom: "35px" }} onClick={() => removePhoto(idx)}>
            <CancelIcon />
          </IconButton>
          <ImagePreview src={photo} key={photo}/>
        </Box>
      );
    });
  };

  return (
    <>
      <Box pt={2}>
        <Label>
            <FileUpload
                type="file"
                multiple
                accept="image/png, image/jpg, image/jpeg"
                onChange={(e) => handleImages(e.target.files)}
            />
            {<AddAPhotoIcon />}{uploadDescription}
        </Label>
      </Box>
      <Box display="flex" flexWrap="wrap" flexDirection="row" maxWidth="70%">
          {getPreviewImages(previewImages)}
      </Box>
    </>
  );
}
