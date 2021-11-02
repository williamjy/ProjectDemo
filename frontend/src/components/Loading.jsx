import React from "react";
import { Box, CircularProgress } from "@material-ui/core";

export default function Loading ({ isLoading }) {
  return (
    isLoading &&
    <Box display="flex"
      justifyContent="center"
      pt={5}
    >
      <CircularProgress color="secondary"/>
    </Box>
  );
}
