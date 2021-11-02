import React, { useState, useEffect } from "react";
import UploadPhotos from "./UploadPhotos";
import { FloatBox } from "../styles/FloatBox";
import { Subtitle } from "../styles/Subtitle";
import { AlignCenter } from "../styles/AlignCenter";
import { Box } from "@material-ui/core";
import SendIcon from "@material-ui/icons/Send";
import {
  validEmail,
  validConfirmPassword,
  validPassword,
  validRequired,
  handleImage,
} from "../utils/helpers";
import { usePlacesWidget } from "react-google-autocomplete";
import Autocomplete from "@material-ui/lab/Autocomplete";
import { ButtonStyled } from "../styles/ButtonStyle";
import AddAPhoto from "@material-ui/icons/AddAPhoto";
import { FileUpload } from "../styles/FileUpload";
import { LinkStyled } from "../styles/LinkStyled";
import { TextFieldStyled } from "../styles/TextFieldStyled";
import { Label } from "../styles/Label";
import { BannerPhoto } from "../styles/BannerPhoto";
import { API_KEY } from "../utils/constants";
import request from "../utils/request";

export default function EateryForm ({
  email,
  setEmail,
  password,
  setPassword,
  confirmPassword,
  setConfirmPassword,
  eateryName,
  setEateryName,
  address,
  setAddress,
  cuisines,
  setCuisines,
  setImages,
  previewImages,
  setPreviewImages,
  isRegister,
  submitForm,
  removeBg = false,
  setTmpProfilePic,
  tmpProfilePic,
}) {
  const [cuisineList, setCuisineList] = useState([]);
  const useGoogleAPI = true;

  const validAddress = () => {
    if (address.value === "") {
      setAddress({ values: "", valid: false });
    }
  };

  const validCuisine = () => {
    console.log(cuisines.value);
    if (Array.isArray(cuisines.value) && !cuisines.value.length) {
      setCuisines({ values: [], valid: false });
    }
  };

  const { ref } = usePlacesWidget({
    apiKey: API_KEY,
    onPlaceSelected: (place) =>
      setAddress({ value: place.formatted_address, valid: true }),
    options: {
      types: ["address"],
      componentRestrictions: { country: "au" },
    },
  });

  useEffect(() => {
    const listOfCuisines = async () => {
      const response = await request.get("list/cuisines");
      const responseData = await response.json();
      if (response.status === 200) {
        setCuisineList(responseData.cuisines);
      }
    };
    listOfCuisines();
  }, []);

  const handleKeyPress = (event) => {
    if (event.key === "Enter") {
      submitForm();
    }
  };

  return (
    <AlignCenter removeBg={removeBg} isEateryForm={true} style={{ padding: "2% 0" }}>
      <FloatBox display="flex" flexDirection="column" alignItems="center">
        <Box pt={2}>
          <Subtitle>
            {isRegister === true ? "Register Eatery" : "Update Eatery"}
          </Subtitle>
        </Box>
        <Box pt={2} width="60%">
          <TextFieldStyled
            aria-label="outlined-basic"
            label="Eatery Name"
            value={eateryName.value}
            onChange={(e) =>
              setEateryName({
                value: e.target.value,
                valid: true,
              })
            }
            onBlur={() => validRequired(eateryName, setEateryName)}
            error={!eateryName.valid}
            helperText={
              eateryName.valid ? "" : "Please enter the name of your eatery with at most 12 characters"
            }
            variant="outlined"
            fullWidth
          />
        </Box>
        <Box pt={2} width="60%">
          <TextFieldStyled
            aria-label="outlined-basic"
            label="Email Address"
            value={email.value}
            onChange={(e) => setEmail({ value: e.target.value, valid: true })}
            onBlur={() => validEmail(email, setEmail)}
            error={!email.valid}
            helperText={email.valid ? "" : "Please enter a valid email"}
            variant="outlined"
            fullWidth
            onKeyPress={handleKeyPress}
          />
        </Box>
        <Box pt={2} width="60%">
          <TextFieldStyled
            aria-label="outlined-basic"
            label="Password"
            type="password"
            onBlur={() => validPassword(password, setPassword)}
            onChange={(e) =>
              setPassword({ value: e.target.value, valid: true })
            }
            error={
              (!password.valid && isRegister) ||
              (!isRegister && !password.valid && password.value.length !== 0)
            }
            helperText={
              password.valid
                ? ""
                : "Please enter a valid password with 1 lowercase, 1 upper case, 1 number with at least 8 characters"
            }
            variant="outlined"
            fullWidth
            onKeyPress={handleKeyPress}
          />
        </Box>
        <Box pt={2} width="60%">
          <TextFieldStyled
            aria-label="outlined-basic"
            label="Confirm Password"
            type="password"
            onChange={(e) =>
              setConfirmPassword({
                value: e.target.value,
                valid: true,
              })
            }
            onBlur={() =>
              validConfirmPassword(
                password,
                confirmPassword,
                setConfirmPassword
              )
            }
            error={!confirmPassword.valid}
            helperText={
              confirmPassword.valid
                ? ""
                : "Please make sure your passwords match"
            }
            variant="outlined"
            fullWidth
            onKeyPress={handleKeyPress}
          />
        </Box>
        <Box pt={2} width="60%">
          <TextFieldStyled
            aria-label="outlined-basic"
            disabled={!useGoogleAPI}
            value={address.value}
            onBlur={validAddress}
            onChange={(e) => setAddress({ value: e.target.value, valid: true })}
            error={!address.valid}
            helperText={
              address.valid ? "" : "Please enter the address of your eatery"
            }
            fullWidth
            variant="outlined"
            inputRef={ref}
            onKeyPress={handleKeyPress}
          />
        </Box>
        <Box pt={2} width="60%">
          <Autocomplete
            multiple
            aria-label="tags-outlined"
            options={cuisineList}
            value={cuisines.value ? cuisines.value : []}
            onChange={(e, allOptions) =>
              setCuisines({ value: allOptions, valid: true })
            }
            onBlur={validCuisine}
            filterSelectedOptions
            renderInput={(params) => (
              <TextFieldStyled
                {...params}
                variant="outlined"
                placeholder="Select Cuisines"
                error={!cuisines.valid}
                helperText={cuisines.valid ? "" : "Please select cuisines"}
                fullWidth
              />
            )}
          />
        </Box>
        <Box pt={2}>
          <Label>
            <FileUpload
              type="file"
              accept="image/png, image/jpg, image/jpeg"
              onChange={(e) => handleImage(e.target.files, setTmpProfilePic)}
            />
            {<AddAPhoto />}{" "}
            {"Display Photo"}
          </Label>
        </Box>
        <Box pt={1} display="flex" justifyContent="center">
          {tmpProfilePic !== null && <BannerPhoto height={50} src={tmpProfilePic} />}
        </Box>
        <UploadPhotos
          setImages={setImages}
          previewImages={previewImages}
          setPreviewImages={setPreviewImages}
          uploadDescription={"Menu Photos"}
        />
        <Box pt={2} display="flex" justifyContent="center" width="100%">
          <ButtonStyled
            widthPercentage={60}
            variant="contained"
            color="primary"
            endIcon={<SendIcon />}
            onClick={submitForm}
          >
            {isRegister ? "Register" : "Update"}
          </ButtonStyled>
        </Box>
        {isRegister && (
          <Box pt={2} pb={4}>
            <LinkStyled to="/">Back to Login</LinkStyled>
          </Box>
        )}
      </FloatBox>
    </AlignCenter>
  );
}
