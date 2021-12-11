-- phpMyAdmin SQL Dump
-- version 4.6.4
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 01, 2018 at 03:30 PM
-- Server version: 5.7.14
-- PHP Version: 5.6.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `sbi`
--

-- --------------------------------------------------------

--
-- Table structure for table `accounts`
--

CREATE TABLE `accounts` (
  `userid` int(11) NOT NULL,
  `user` varchar(11) NOT NULL,
  `accno` bigint(20) NOT NULL,
  `cifno` bigint(20) NOT NULL,
  `balance` int(11) NOT NULL,
  `acctype` int(11) NOT NULL,
  `ifsc` varchar(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `accounts`
--

INSERT INTO `accounts` (`userid`, `user`, `accno`, `cifno`, `balance`, `acctype`, `ifsc`) VALUES
(1, 'intel', 18923467823, 18337119023, 386, 1, 'SBIN0009043'),
(2, 'parth', 17933577822, 15397229123, 2961, 1, 'SBIN0016682'),
(1, 'intel', 17823467823, 18337119023, 416, 1, 'SBIN0008043'),
(3, 'naveen', 26825467832, 15278229123, 505, 1, 'SBIN0007043');

-- --------------------------------------------------------

--
-- Table structure for table `linked`
--

CREATE TABLE `linked` (
  `userid` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `accno` bigint(20) NOT NULL,
  `ifsc` varchar(11) NOT NULL,
  `createdat` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `linked`
--

INSERT INTO `linked` (`userid`, `name`, `accno`, `ifsc`, `createdat`) VALUES
(1, 'Intel Corporation', 159214268236218, 'UTIB0003095', '2018-03-27 15:15:37');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `name` varchar(30) NOT NULL,
  `username` varchar(30) NOT NULL,
  `passwd` varchar(30) NOT NULL,
  `token` varchar(100) NOT NULL,
  `cifno` bigint(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `name`, `username`, `passwd`, `token`, `cifno`) VALUES
(1, 'Intel ', 'intel', 'password', '6c3dbd545aa4fa7e5a2c73c36c037aded20ba3ed6d68c4275133a1c1cbc52337', 18337119023),
(2, 'Parth Pratim Chatterjee', 'parth', 'password', '848f1c1401bbbf860e8e9da7756d3f38d19a8f8c34fe5222dba3fc40d529a6f4', 15397229123),
(3, 'Naveen Bansal', 'naveen', 'password', '748f1fuck1bbbf860e8e9da7756d3f38d19a8f8c34fe5222dba3fc40d529a6f4', 15278229123),
(4, 'Jitendra Kumar', 'jeetu', 'password', '148f1coder1bbbf860e8e9da7756d3f38d19a8f8c34fe5212dba3fc40d529a6f4', 12278227123),
(5, 'Manvi gagroo', 'manvi', 'password', '648f1bo4ed1bbbf860e8z6da7756d3f38d19a8f8c34fe5212dba3fc40d529a6f4', 14268227124);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `accounts`
--
ALTER TABLE `accounts`
  ADD PRIMARY KEY (`accno`);

--
-- Indexes for table `linked`
--
ALTER TABLE `linked`
  ADD PRIMARY KEY (`createdat`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `cifno` (`cifno`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
