import axios from 'axios';

const API_BASE = process.env.REACT_APP_BASE_URL;

export const getCustomers = (page = 0, size = 5) =>
  axios.get(`${API_BASE}/customers?page=${page}&size=${size}`);

export const getCustomerById = (id) =>
  axios.get(`${API_BASE}/customers/${id}`);

export const createCustomer = (data) =>
  axios.post(`${API_BASE}/customers`,data);

export const updateCustomer = (id, data) =>
  axios.put(`${API_BASE}/customers/${id}`, data);


export const getAllCountries = () =>
  axios.get(`${API_BASE}/countries`);

export const getAllCities = () =>
  axios.get(`${API_BASE}/cities`);

export const bulkCreateCustomers = (file) => {
  const formData = new FormData();
  formData.append('file', file);
  return axios.post(`${API_BASE}/customers/bulk-create`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};


