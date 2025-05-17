import React, { useEffect, useState } from 'react';
import {
  Button,
  TextField,
  Box,
  Grid,
  Typography,
  MenuItem,
  CircularProgress,
} from '@mui/material';
import { useFormik } from 'formik';
import * as yup from 'yup';
import { getAllCities, getAllCountries,bulkCreateCustomers } from '../api/customerService';

const validationSchema = yup.object({
  name: yup.string().required('Name is required'),
  nic: yup.string().required('NIC is required'),
  dateOfBirth: yup.string().required('Date of Birth is required'),
  mobileNumber1: yup.string().required('At least one mobile number is required'),
  addressLine1: yup.string().required('Address Line 1 is required'),
  cityId: yup.number().required('City is required'),
  countryId: yup.number().required('Country is required'),
});


const CustomerForm =({ initialValues, onSubmit, onCancel })=>{

  const [cities, setCities] = useState([]);
  const [countries, setCountries] = useState([]);
  const [loading, setLoading] = useState(true);
  const [formInitialized, setFormInitialized] = useState(false);

  // bulk create
  const [bulkFile, setBulkFile] = useState(null);
  const [uploading, setUploading] = useState(false);

  //bulk create api call
  const handleBulkUpload = async () => {
  if (!bulkFile) {
    alert('Please select a file first.');
    return;
  }

  setUploading(true);
  try {
    const res = await bulkCreateCustomers(bulkFile);
    alert('Bulk upload successful!');
    console.log(res.data);
  } catch (err) {
    console.error(err);
    alert('Bulk upload failed.');
  } finally {
    setUploading(false);
    setBulkFile(null);
  }
};



  const getFormValues = (values) => {
    if (!values) {
      return {
        name: '',
        nic: '',
        dateOfBirth: '',
        mobileNumber1: '',
        mobileNumber2: '',
        addressLine1: '',
        addressLine2: '',
        cityId: '',
        countryId: '',
      };
    }

    const address = values.addresses?.[0] || {};
    
    return {
      name: values.name || '',
      nic: values.nic || '',
      dateOfBirth: values.dateOfBirth || '',
      mobileNumber1: values.mobileNumbers?.[0] || '',
      mobileNumber2: values.mobileNumbers?.[1] || '',
      addressLine1: address.addressLine1 || '',
      addressLine2: address.addressLine2 || '',
      cityId: address.city?.id || address.cityId || '',
      countryId: address.country?.id || address.countryId || '',
    };
  };

  const formik = useFormik({
    initialValues: getFormValues(initialValues),
    validationSchema,
    onSubmit: (values) => {
      const payload = {
        name: values.name,
        nic: values.nic,
        dateOfBirth: values.dateOfBirth,
        mobileNumbers: [
          values.mobileNumber1,
          ...(values.mobileNumber2 ? [values.mobileNumber2] : []),
        ],
        addresses: [
          {
            addressLine1: values.addressLine1,
            addressLine2: values.addressLine2 || null,
            cityId: Number(values.cityId),
            countryId: Number(values.countryId),
          },
        ],
        parentId: initialValues?.parentId || [],
      };
      onSubmit(payload);
    },
    enableReinitialize: true,
  });

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [cityRes, countryRes] = await Promise.all([
          getAllCities(),
          getAllCountries(),
        ]);
        setCities(cityRes.data.data);
        setCountries(countryRes.data.data);
        setFormInitialized(true);
      } catch (err) {
        console.error('Error fetching location data', err);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  useEffect(() => {
    if (formInitialized && initialValues) {
      formik.setValues(getFormValues(initialValues));
    }
  }, [formInitialized, initialValues]);

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box component="form" onSubmit={formik.handleSubmit} sx={{ my: 2 }}>
      <Typography variant="h6" gutterBottom>Customer Info</Typography>
      <Grid container spacing={2}>
        <Grid item xs={12}>
          <TextField
            fullWidth
            id="name"
            name="name"
            label="Name"
            value={formik.values.name}
            onChange={formik.handleChange}
            error={formik.touched.name && !!formik.errors.name}
            helperText={formik.touched.name && formik.errors.name}
          />
        </Grid>
        <Grid item xs={12}>
          <TextField
            fullWidth
            id="nic"
            name="nic"
            label="NIC"
            value={formik.values.nic}
            onChange={formik.handleChange}
            error={formik.touched.nic && !!formik.errors.nic}
            helperText={formik.touched.nic && formik.errors.nic}
          />
        </Grid>
        <Grid item xs={12}>
          <TextField
            fullWidth
            id="dateOfBirth"
            name="dateOfBirth"
            label="Date of Birth"
            type="date"
            InputLabelProps={{ shrink: true }}
            value={formik.values.dateOfBirth}
            onChange={formik.handleChange}
            error={formik.touched.dateOfBirth && !!formik.errors.dateOfBirth}
            helperText={formik.touched.dateOfBirth && formik.errors.dateOfBirth}
          />
        </Grid>
        <Grid item xs={12} sm={6}>
          <TextField
            fullWidth
            id="mobileNumber1"
            name="mobileNumber1"
            label="Mobile Number 1"
            value={formik.values.mobileNumber1}
            onChange={formik.handleChange}
            error={formik.touched.mobileNumber1 && !!formik.errors.mobileNumber1}
            helperText={formik.touched.mobileNumber1 && formik.errors.mobileNumber1}
          />
        </Grid>
        <Grid item xs={12} sm={6}>
          <TextField
            fullWidth
            id="mobileNumber2"
            name="mobileNumber2"
            label="Mobile Number 2 (optional)"
            value={formik.values.mobileNumber2}
            onChange={formik.handleChange}
          />
        </Grid>

        <Grid item xs={12}>
          <Typography variant="h6" gutterBottom>Address</Typography>
        </Grid>
        <Grid item xs={12}>
          <TextField
            fullWidth
            id="addressLine1"
            name="addressLine1"
            label="Address Line 1"
            value={formik.values.addressLine1}
            onChange={formik.handleChange}
            error={formik.touched.addressLine1 && !!formik.errors.addressLine1}
            helperText={formik.touched.addressLine1 && formik.errors.addressLine1}
          />
        </Grid>
        <Grid item xs={12}>
          <TextField
            fullWidth
            id="addressLine2"
            name="addressLine2"
            label="Address Line 2"
            value={formik.values.addressLine2}
            onChange={formik.handleChange}
          />
        </Grid>
        <Grid item xs={6}>
          <TextField
            select
            fullWidth
            id="cityId"
            name="cityId"
            label="City"
            value={formik.values.cityId}
            onChange={formik.handleChange}
            error={formik.touched.cityId && !!formik.errors.cityId}
            helperText={formik.touched.cityId && formik.errors.cityId}
          >
            <MenuItem value="">
              <em>Select a city</em>
            </MenuItem>
            {cities.map((city) => (
              <MenuItem key={city.id} value={city.id}>
                {city.name}
              </MenuItem>
            ))}
          </TextField>
        </Grid>
        <Grid item xs={6}>
          <TextField
            select
            fullWidth
            id="countryId"
            name="countryId"
            label="Country"
            value={formik.values.countryId}
            onChange={formik.handleChange}
            error={formik.touched.countryId && !!formik.errors.countryId}
            helperText={formik.touched.countryId && formik.errors.countryId}
          >
            <MenuItem value="">
              <em>Select a country</em>
            </MenuItem>
            {countries.map((country) => (
              <MenuItem key={country.id} value={country.id}>
                {country.name}
              </MenuItem>
            ))}
          </TextField>
        </Grid>
      </Grid>

      <Box sx={{ mt: 3 }}>
        <Button variant="contained" type="submit" sx={{ mr: 2 }}>
          Save
        </Button>
        <Button variant="outlined" onClick={onCancel}>
          Cancel
        </Button>

        {/* Bulk upload input */}
       <Box sx={{ mt: 3 }}>
        <Typography variant="h6" sx={{ mb: 1 }}>Bulk Upload</Typography>
          <input
            type="file"
            accept=".csv, .xlsx"
            onChange={(e) => setBulkFile(e.target.files[0])}
          />
        <Button
            variant="outlined"
            color="secondary"
            sx={{ mt: 1 }}
            disabled={uploading}
            onClick={handleBulkUpload}
        >
            {uploading ? 'Uploading...' : 'Upload Bulk File'}
        </Button>
      </Box>
      </Box>
    </Box>
  );

}
export default CustomerForm