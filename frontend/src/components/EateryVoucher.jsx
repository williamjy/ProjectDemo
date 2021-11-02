import React, { useContext, useState } from "react";
import { StoreContext } from "../utils/store";
import { Redirect } from "react-router-dom";
import { Box, IconButton, Grid } from "@material-ui/core";
import DeleteIcon from "@material-ui/icons/Delete";
import EditIcon from "@material-ui/icons/Edit";
import ConfirmModal from "./ConfirmModal";
import EditCreateVoucher from "../components/EditCreateVoucher";
import Countdown from "react-countdown";
import request from "../utils/request";

export default function EateryVoucher ({
  eateryId,
  voucherId,
  isOneOff,
  discount,
  isDineIn,
  vouchersLeft,
  date,
  startTime,
  endTime,
  timeRemaining,
  nextUpdate,
  isActive,
  isRedeemable,
  refreshList
}) {
  // console.log(date);

  const convertToDateTime = (date, time) => {
    const datetime = new Date(date);
    const [hours, minutes] = time.split(":");
    // This accounts for the timezone difference between +10AEST and +0GMT
    datetime.setHours(hours);
    datetime.setMinutes(minutes - datetime.getTimezoneOffset());
    console.log(date);
    console.log(hours, minutes);
    console.log(datetime);
    return datetime;
  };
  const startDateTime = convertToDateTime(date, startTime);
  const endDateTime = convertToDateTime(date, endTime);

  // console.log(new Date().getMilliseconds);
  // console.log("HLSKDJHFSDKJFH");
  // const tmpStartCheck = new Date(startDateTime).setMinutes(startDateTime.getMinutes() - startDateTime.getTimezoneOffset());
  // console.log(tmpStartCheck);
  // console.log(Date.now() > (tmpStartCheck.getMilliseconds));

  const context = useContext(StoreContext);
  const auth = context.auth[0];
  const isDiner = context.isDiner[0];
  const setAlertOptions = context.alert[1];
  console.log(auth);
  console.log(isDiner);
  const [openDeleteModal, setOpenDeleteModal] = useState(false);
  const [editCreateModal, setEditCreateModal] = useState(false);

  if (auth === null) return <Redirect to="/" />;
  if (isDiner === "true") return <Redirect to="/DinerLanding" />;
  console.log(isDiner);

  const removeVoucher = async (id) => {
    const response = await request.delete(`eatery/voucher?id=${voucherId}`, {}, auth);
    const responseData = await response.json();
    if (response.status === 200) {
      setAlertOptions({ showAlert: true, variant: "success", message: responseData.message });
      refreshList();
    } else {
      setAlertOptions({ showAlert: true, variant: "error", message: responseData.message });
    }
    setOpenDeleteModal(false);
  };

  return (
    <Box bgcolor="#FFF9F7" margin="20px" border="1px dotted #FF845B" >
      <Grid container direction="row" justifyContent="center" alignItems="center" border="3px solid #4F4846" margin="20px">
        <Grid item display="flex" flexDirection="column" xs={4}>
          <Box display="flex" justifyContent="center" alignItems="center">
            <h1>{discount}% off - {isDineIn ? "Dine in" : "Takeaway"}</h1>
          </Box>
        </Grid>
        <Grid item display="flex" justifyContent="center" xs={5}>
          <Box display="flex" flexDirection="column" pl={10}>
            {
              !isOneOff
                ? <h3 style={{ margin: "5px 0px", color: "#96AE33", textDecoration: "underline" }}>Weekly deal</h3>
                : <h3 style={{ margin: "5px 0px", color: "#96AE33", textDecoration: "underline" }}>One Off deal</h3>
            }
            {
              (isRedeemable || isActive) &&
              <h4 style={{ margin: "5px 0px" }}>{vouchersLeft} vouchers remaining...</h4>
            }
            {
              isRedeemable &&
              <Countdown
                onComplete={refreshList}
                date={Date.now() + timeRemaining}
              />
            }
            {
              !isRedeemable && isActive &&
              <h4 style={{ margin: "5px 0px" }}>Deal starts at {date} {startTime}</h4>
            }
            {
              !isRedeemable && !isActive &&
              <>
                <h4 style={{ margin: "5px 0px" }}>Deal will become available </h4>
                <h4 style={{ margin: "5px 0px" }}>again at {nextUpdate} {startTime}</h4>
              </>
            }
          </Box>
        </Grid>
        <Grid item display="flex" flexDirection="column" justifyContent="center" xs={3}>
          <Box display="flex" justifyContent="center">
            <IconButton onClick={() => {}} style={{ color: "#FF855B" }}>
              <EditIcon fontSize="large"
                onClick={() => setEditCreateModal(true)}
              />
            </IconButton>
            <IconButton onClick={() => {}} style={{ color: "#FF855B" }}>
              <DeleteIcon fontSize="large"
                onClick={() => setOpenDeleteModal(true)}
              />
            </IconButton>
          </Box>
        </Grid>
      </Grid>
      <ConfirmModal open={openDeleteModal}
        handleClose={() => setOpenDeleteModal(false)}
        title={"Delete voucher?"}
        message={`Customers will no longer be able to book the ${vouchersLeft} vouchers remaining, are you sure you want to delete?`}
        handleConfirm={() => removeVoucher(voucherId)}>
      </ConfirmModal>
      <EditCreateVoucher eateryId={eateryId} voucherId={voucherId}
        open={editCreateModal}
        setOpen={setEditCreateModal}
        isEdit={true}
        initOneOff={isOneOff ? 0 : 1} initDineIn={isDineIn ? "true" : "false"}
        initDiscount={discount}
        initQuantity={vouchersLeft}
        initStartTime={startDateTime}
        initEndTime={endDateTime}
        refreshList={refreshList}
      ></EditCreateVoucher>
    </Box>
  );
}
