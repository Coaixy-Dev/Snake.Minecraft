extern crate piston_window;
use piston_window::*;
use rand::Rng;
use std::time::{Duration, Instant};

const BLOCK_SIZE: f64 = 20.0;
const WIDTH: u32 = 30;
const HEIGHT: u32 = 20;
const UPDATE_INTERVAL: Duration = Duration::from_millis(100);

#[derive(Clone, Copy, PartialEq)]
enum Direction {
    Up,
    Down,
    Left,
    Right,
}

struct Snake {
    body: Vec<[i32; 2]>,
    direction: Direction,
}

impl Snake {
    fn new() -> Snake {
        Snake {
            body: vec![[10, 10], [10, 11], [10, 12]],
            direction: Direction::Up,
        }
    }

    fn update(&mut self) {
        let mut new_head = self.body[0];
        match self.direction {
            Direction::Up => new_head[1] -= 1,
            Direction::Down => new_head[1] += 1,
            Direction::Left => new_head[0] -= 1,
            Direction::Right => new_head[0] += 1,
        }
        self.body.insert(0, new_head);
        self.body.pop();
    }

    fn grow(&mut self) {
        let tail = *self.body.last().unwrap();
        self.body.push(tail);
    }

    fn change_direction(&mut self, dir: Direction) {
        self.direction = dir;
    }

    fn check_collision_with_wall(&self) -> bool {
        let head = self.body[0];
        head[0] < 0 || head[0] >= WIDTH as i32 || head[1] < 0 || head[1] >= HEIGHT as i32
    }
}

struct Food {
    position: [i32; 2],
}

impl Food {
    fn new() -> Food {
        Food { position: [15, 10] }
    }

    fn generate_new(&mut self) {
        let mut rng = rand::thread_rng();
        self.position = [
            rng.gen_range(0..WIDTH as i32),
            rng.gen_range(0..HEIGHT as i32),
        ];
    }
}

fn draw_block(color: [f32; 4], x: i32, y: i32, context: Context, g: &mut G2d) {
    let gui_x = (x as f64) * BLOCK_SIZE;
    let gui_y = (y as f64) * BLOCK_SIZE;

    rectangle(
        color,
        [gui_x, gui_y, BLOCK_SIZE, BLOCK_SIZE],
        context.transform,
        g,
    );
}

fn main() {
    let mut window: PistonWindow = WindowSettings::new(
        "Snake Game",
        [WIDTH * BLOCK_SIZE as u32, HEIGHT * BLOCK_SIZE as u32],
    )
    .exit_on_esc(true)
    .build()
    .unwrap();

    let mut snake = Snake::new();
    let mut food = Food::new();
    let mut game_over = false;
    let mut last_update = Instant::now();

    while let Some(event) = window.next() {
        if let Some(Button::Keyboard(key)) = event.press_args() {
            let new_direction = match key {
                Key::Up if snake.direction != Direction::Down => Some(Direction::Up),
                Key::Down if snake.direction != Direction::Up => Some(Direction::Down),
                Key::Left if snake.direction != Direction::Right => Some(Direction::Left),
                Key::Right if snake.direction != Direction::Left => Some(Direction::Right),
                _ => None,
            };

            if let Some(dir) = new_direction {
                snake.change_direction(dir);
            }
        }

        if let Some(_) = event.update_args() {
            if last_update.elapsed() >= UPDATE_INTERVAL {
                last_update = Instant::now();

                if !game_over {
                    snake.update();

                    if snake.body[0] == food.position {
                        snake.grow();
                        food.generate_new();
                    }

                    if snake.body[1..].contains(&snake.body[0]) || snake.check_collision_with_wall()
                    {
                        game_over = true;
                    }
                }
            }
        }

        window.draw_2d(&event, |context, g, _| {
            clear([0.0, 0.0, 0.0, 1.0], g);

            for block in &snake.body {
                draw_block([0.0, 1.0, 0.0, 1.0], block[0], block[1], context, g);
            }

            draw_block(
                [1.0, 0.0, 0.0, 1.0],
                food.position[0],
                food.position[1],
                context,
                g,
            );
        });
    }
}
