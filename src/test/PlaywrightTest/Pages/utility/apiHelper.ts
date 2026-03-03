import axios from 'axios';

export const  getAccessToken = async ( idamUrl, email: string,  password: string  ) => {
  try {
    const axiosConfig = {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      timeout: 30000,  // - 30 second timeout
    };
    const url = `${idamUrl}/loginUser?username=${email}&password=${password}`;
    const response = await axios.post(url, {}, axiosConfig);
    const token = response.data.access_token;
    return token;
  } catch (error) {
    console.error('Auth failed:', error);
    throw error;
  }
};

export const  getServiceAuthToken = async (url) => {
  try {
    const axiosConfig = {
      headers: {
        'accept': '*/*',
        'Content-Type': 'application/json',
      },
      timeout: 30000,  // - 30 second timeout
    };
    const body = {
      microservice: 'probate_backend'
    };
    const response = await axios.post(url, body, axiosConfig);
    return response.data;
  } catch (error) {
    console.error('Auth failed:', error);
    throw error;
  }
};
