# Circle of Friends

Video demo:
https://www.youtube.com/watch?v=mVtJpZ6Om4I

This is Circle of Friends, a social media app where everybody is
friends with everybody! You are connected to everyone on the app
from the moment you hit "Register."

From the get-go, you are following (and being followed by) every
other user on the app. They see all of your posts, and you see
all of theirs. You can react to others' posts by liking or
disliking them, as well - try to get as many likes as you can!

The app is an exercise in web backends. The backend consists of
a MySQL database with two tables - 'users' (holds users and their
passwords for login) and 'posts' (holds the posts that each user
writes, along with their timestamp and number of likes/dislikes).

PHP is used to communicate between your app and the MySQL
database. The database is hosted on mysql.cs.binghamton.edu;
because of this, you can't directly access the database unless
you're on the University's wifi. However, by using the PHP
scripts that are hosted on cs.binghamton.edu, you can use this
app anywhere.
