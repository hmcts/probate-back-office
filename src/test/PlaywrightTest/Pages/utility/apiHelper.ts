import axios from 'axios';

export const  getAccessToken = async ( idamUrl, email: string,  password: string  ) => {
  try {
    console.log('=== API REQUEST DEBUG ===');
    console.log('Base URL:', idamUrl);
    console.log('Email:', email);
    console.log('Email length:', email?.length);
    console.log('Password length:', password?.length);
    console.log('Email is undefined:', email === undefined);
    console.log('Password is undefined:', password === undefined);
    console.log('Email trimmed length:', email?.trim().length);
    console.log('Password trimmed length:', password?.trim().length);
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

export const  getServiceAuthToken = async (url, microservice: string) => {
  try {
    const axiosConfig = {
      headers: {
        'accept': '*/*',
        'Content-Type': 'application/json',
      },
      timeout: 30000,  // - 30 second timeout
    };
    const body = {
      microservice: `${microservice}`
    };
    const response = await axios.post(url, body, axiosConfig);
    return response.data;
  } catch (error) {
    console.error('Auth failed:', error);
    throw error;
  }
};
