import React, { useState, useContext } from "react";
import { Dialog, DialogContent, Box, DialogActions, Tabs, Tab, Radio, RadioGroup, FormControlLabel } from "@material-ui/core";
import { StoreContext } from "../utils/store";
import { validRequired, Transition } from "../utils/helpers";
import { ModalButton } from "../styles/ModalButton";
import { DialogTitleStyled } from "../styles/DialogTitleStyled";
import { TextFieldStyled } from "../styles/TextFieldStyled";
import request from "../utils/request";

export default function EditCreateVoucher ({ eateryId, voucherId, open, setOpen, initOneOff = 0, initDineIn = "true", initDiscount = "", initQuantity = "", initStartTime = "", initEndTime = "", isEdit, refreshList }) {
  const date = new Date();

  const context = useContext(StoreContext);
  const setAlertOptions = context.alert[1];
  const token = context.auth[0];
  const defaultState = (initialValue = "") => {
    return { value: initialValue, valid: true };
  };
  // isOneOff will either be 0 or 1, used for tabs - 1 for is weekly, 0 for is oneoff
  const [isOneOff, setIsOneOff] = useState(initOneOff);
  const [isDineIn, setisDineIn] = useState(defaultState(initDineIn));
  const [discount, setDiscount] = useState(defaultState(initDiscount));
  const [quantity, setQuantity] = useState(defaultState(initQuantity));
  const [startDateTime, setStartDateTime] = useState(defaultState(initStartTime === ""
    ? date.toISOString().split("T")[0] + `T${date.getHours()}:${date.getMinutes()}`
    : initStartTime.toISOString().slice(0, -1)));
  const [endDateTime, setEndDateTime] = useState(defaultState(initEndTime === ""
    ? date.toISOString().split("T")[0] + `T${date.getHours()}:${date.getMinutes()}`
    : initEndTime.toISOString().slice(0, -1)));

  const checkFormValid = () => {
    validRequired(discount, setDiscount);
    validRequired(quantity, setQuantity);
    checkValidEndDate();
    if (discount.value === "" || quantity.value === "" || !checkValidEndDate(false) || !startDateTime.valid || !endDateTime.valid) {
      return false;
    }
    return true;
  };

  const handleEditCreateVoucher = async (isEdit) => {
    // Must first ensure that all the fields are valid
    if (!checkFormValid()) {
      return;
    }
    console.log("This will create the voucher");
    console.log(startDateTime.value);
    const timeArry = startDateTime.value.split("T")[1].split(":");
    const startMinute = parseInt(timeArry[0] * 60) + parseInt(timeArry[1]);
    const endMinute = (new Date(endDateTime.value) - new Date(startDateTime.value)) / 60000 + startMinute;
    console.log(startDateTime.value.split("T")[0]);
    console.log(startMinute);
    console.log(endMinute);
    const reqType = isEdit ? "PUT" : "POST";
    const body = {
      eateryId: eateryId,
      eatingStyle: (isDineIn.value === "true" ? "DineIn" : "Takeaway"),
      discount: discount.value,
      quantity: quantity.value,
      isRecurring: (isOneOff !== 0),
      date: startDateTime.value.split("T")[0],
      startMinute: startMinute,
      endMinute: endMinute
    };
    if (isEdit) {
      body.id = voucherId;
    }
    let response;
    if (reqType === "PUT") {
      response = await request.put("eatery/voucher", body, token);
    } else {
      response = await request.post("eatery/voucher", body, token);
    }
    const responseData = await response.json();
    if (response.status === 200) {
      setAlertOptions({ showAlert: true, variant: "success", message: responseData.message });
      refreshList();
    } else {
      setAlertOptions({ showAlert: true, variant: "error", message: responseData.message });
    }
    setOpen(false);
  };

  const checkValidEndDate = (setValue = true) => {
    const now = new Date();
    const start = new Date(startDateTime.value);
    const end = new Date(endDateTime.value);
    if (start > end || start < now) {
      if (setValue) {
        setStartDateTime({ ...startDateTime, valid: false });
      } else {
        return false;
      }
    } else if ((end - start) > 86400000 || (end - start) < 1800000) {
      if (setValue) {
        setEndDateTime({ ...endDateTime, valid: false });
      } else {
        return false;
      }
    }
    return true;
  };

  return (
    <>
      <Dialog aria-labelledby="customized-dialog-title" open={open} onClose={() => setOpen(false)}
        TransitionComponent={Transition}
        keepMounted>
        <DialogTitleStyled>
          {isEdit ? "Edit Voucher" : "Create Voucher"}
        </DialogTitleStyled>
        <DialogContent dividers>
          <Box>
            <Tabs value={isOneOff} aria-label="simple tabs example">
              <Tab label="One-off deal" onClick={() => setIsOneOff(0)} />
              <Tab label="Weekly deal" onClick={() => setIsOneOff(1)} />
            </Tabs>
          </Box>
          <Box display="flex" flexDirection="column" alignItems="center">
            <Box pt={2}>
              <RadioGroup row value={isDineIn.value} onChange={(e) => setisDineIn({ ...isDineIn, value: e.target.value })}>
                <FormControlLabel value="true" control={<Radio />} label="Dine in" />
                <FormControlLabel value="false" control={<Radio />} label="Takeaway" />
              </RadioGroup>
            </Box>
            <Box py={2} width="270px">
              <TextFieldStyled aria-label="outlined-basic"
                label="Discount (%)"
                type="number"
                onChange={(e) => e.target.value > 0 && e.target.value <= 100
                  ? setDiscount({
                    value: e.target.value,
                    valid: true
                  })
                  : (e.target.value > 100
                      ? null
                      : setDiscount({
                        value: "",
                        valid: true
                      }))
                }
                allowNegative={false}
                error={!discount.valid}
                helperText={
                  discount.valid ? "" : "Please enter a discount percentage"
                }
                onBlur={() => {
                  validRequired(discount, setDiscount);
                }}
                value={discount.value}
                variant="outlined"
                fullWidth
              />
            </Box>
            <Box py={2} width="270px">
              <TextFieldStyled aria-label="outlined-basic"
                label="Quantity"
                type="number"
                onChange={(e) => e.target.value > 0
                  ? setQuantity({
                    value: e.target.value,
                    valid: true
                  })
                  : setQuantity({
                    value: "",
                    valid: true
                  })
                }
                allowNegative={false}
                error={!quantity.valid}
                helperText={
                  quantity.valid ? "" : "Please enter the number of vouchers you would like to offer"
                }
                onBlur={() => {
                  validRequired(quantity, setQuantity);
                }}
                value={quantity.value}
                variant="outlined"
                fullWidth
            />
            </Box>
            <Box py={2}>
              {/* Making use of datetime local type does not work well for all browsers */}
              <TextFieldStyled aria-label="outlined-basic"
                label="Start at:"
                type="datetime-local"
                onChange={(e) => {
                  setStartDateTime({
                    value: e.target.value,
                    valid: true
                  });
                  setEndDateTime({ ...endDateTime, valid: true });
                }
                }
                error={!startDateTime.valid}
                helperText={
                  startDateTime.valid ? "" : "Voucher must start before it ends and not be in the past"
                }
                onBlur={() => {
                  checkValidEndDate();
                }}
                value={startDateTime.value}
                variant="outlined"
                fullWidth
              />
            </Box>
            <Box py={2}>
              <TextFieldStyled aria-label="outlined-basic"
                label="End at:"
                type="datetime-local"
                onChange={(e) => {
                  setEndDateTime({
                    value: e.target.value,
                    valid: true
                  });
                  setStartDateTime({ ...startDateTime, valid: true });
                }
                }
                error={!endDateTime.valid}
                helperText={
                  endDateTime.valid ? "" : "Deal must last at least 30 minutes and at most 1 day"
                }
                onBlur={() => {
                  checkValidEndDate();
                }}
                value={endDateTime.value}
                variant="outlined"
                fullWidth
              />
            </Box>
          </Box>
        </DialogContent>
        <DialogActions>
          {/* Set the things below back to their default states here */}
          <ModalButton autoFocus onClick={() => { setOpen(false); }} color="primary">
            Cancel
          </ModalButton>
          <ModalButton autoFocus onClick={() => handleEditCreateVoucher(isEdit)} color="primary">
            {isEdit ? "Save changes" : "Create voucher"}
          </ModalButton>
        </DialogActions>
      </Dialog>
    </>
  );
}
